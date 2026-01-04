import { create } from 'zustand';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { API_END_POINT } from '../api/Api';

let stompClient = null;
let reconnectTimeout = null;
let isReconnecting = false;

export const alertStateStore = create((set, get) => ({
    notifications: [],
    isConnected: false,
    isConnecting: false,
    reconnectAttempts: 0,
    maxReconnectAttempts: 5,
    connectionError: null,

    // 알림 추가
    addNotification: (notification) => set((state) => ({
        notifications: [...state.notifications,
        { ...notification, id: notification.id || Date.now() + Math.random().toString(36).substring(2, 11) }
        ]
    })),

    // 알림 목록 초기화
    clearNotifications: () => set({ notifications: [] }),

    // 재연결 로직
    scheduleReconnect: () => {
        const state = get();

        if (isReconnecting) {
            console.log('🔄 Already reconnecting...');
            return;
        }

        if (state.reconnectAttempts >= state.maxReconnectAttempts) {
            console.error('❌ Max reconnect attempts reached');
            set({
                reconnectAttempts: 0,
                connectionError: 'Maximum reconnection attempts reached'
            });
            isReconnecting = false;
            return;
        }

        isReconnecting = true;
        const delay = Math.min(1000 * Math.pow(2, state.reconnectAttempts), 30000); // 최대 30초

        console.log(`🔄 Reconnecting in ${delay}ms... (Attempt ${state.reconnectAttempts + 1}/${state.maxReconnectAttempts})`);

        if (reconnectTimeout) clearTimeout(reconnectTimeout);

        reconnectTimeout = setTimeout(() => {
            set((state) => ({ reconnectAttempts: state.reconnectAttempts + 1 }));
            isReconnecting = false;
            get().connect();
        }, delay);
    },

    // 웹소켓 연결
    connect: () => {
        const state = get();

        // 이미 연결된 상태면 중복 연결 방지
        if (state.isConnected || state.isConnecting) {
            console.log('⚠️ Already connected or connecting');
            return;
        }

        set({ isConnecting: true, connectionError: null });

        try {
            // 기존 클라이언트 정리
            if (stompClient) {
                try {
                    stompClient.deactivate();
                } catch (e) {
                    console.warn('Previous client deactivation failed:', e);
                }
                stompClient = null;
            }

            stompClient = new Client({
                webSocketFactory: () => {
                    console.log('🌐 Creating SockJS connection to:', API_END_POINT.alert.alert_connect);
                    return new SockJS(API_END_POINT.alert.alert_connect, null, {
                        withCredentials: true
                    });
                },
                debug: (str) => {
                    // 디버그 로그 
                    console.log('STOMP:', str);
                },
                reconnectDelay: 0, // 자동 재연결 비활성화 (직접 관리)
                heartbeatIncoming: 10000,
                heartbeatOutgoing: 10000,
                onConnect: (frame) => {
                    console.log('✅ WebSocket Connected Successfully');
                    console.log('📋 Connection Frame:', frame);
                    set({
                        isConnected: true,
                        isConnecting: false,
                        reconnectAttempts: 0,
                        connectionError: null
                    });
                    isReconnecting = false;


                    const subscriptionPath = API_END_POINT.alert.alert_subscribe;
                    console.log('📡 Subscribing to:', subscriptionPath);

                    // 알림 구독
                    stompClient.subscribe(subscriptionPath, (message) => {
                        try {
                            console.log('📨 Raw message received:', message);
                            console.log('📨 Message body:', message.body);
                            console.log('📨 Message headers:', message.headers);

                            let content = message.body;

                            // JSON 파싱 시도
                            try {
                                const parsed = JSON.parse(message.body);
                                if (typeof parsed === 'object' && parsed !== null) {
                                    content = parsed.content || parsed.message || parsed;
                                } else {
                                    content = parsed;
                                }
                            } catch (e) {
                                content = message.body;
                            }

                            // 헤더 정보 추출
                            const sender = message.headers['sender'] || 'System';
                            const priority = message.headers['priority'] || 'NORMAL';
                            const eventType = message.headers['eventType'] || 'UNKNOWN';
                            const eventSubType = message.headers['eventSubType'] || 'UNKNOWN';

                            console.log(`📬 신규 알림 [${eventType}/${eventSubType}][우선순위: ${priority}]:`, content);

                            get().addNotification({
                                content,
                                sender,
                                priority,
                                eventType,
                                eventSubType,
                                timestamp: new Date().toLocaleTimeString()
                            });
                        } catch (error) {
                            console.error('❌ Error processing notification:', error);
                        }
                    });
                },

                onStompError: (frame) => {
                    console.error('❌ STOMP Error Frame:', frame);
                    console.error('❌ Error Message:', frame.headers['message']);
                    console.error('❌ Error Body:', frame.body);
                    set({
                        isConnected: false,
                        isConnecting: false,
                        connectionError: frame.headers['message'] || 'STOMP error occurred'
                    });

                    // 인증 오류인 경우 재연결 시도 안함
                    if (frame.headers['message']?.includes('Authentication') ||
                        frame.headers['message']?.includes('Authorization')) {
                        console.error('❌ Authentication failed - not reconnecting');
                        return;
                    }

                    get().scheduleReconnect();
                },

                onWebSocketClose: (event) => {
                    console.error('❌ WebSocket Closed:', event);
                    console.error('Close Code:', event.code, 'Reason:', event.reason);

                    set({
                        isConnected: false,
                        isConnecting: false,
                        connectionError: `Connection closed: ${event.reason || event.code}`
                    });

                    // 재연결 시도
                    if (event.code !== 1000) {
                        get().scheduleReconnect();
                    }
                },

                onWebSocketError: (event) => {
                    console.error('❌ WebSocket Error:', event);
                    set({
                        connectionError: 'WebSocket connection error'
                    });
                },

                onDisconnect: () => {
                    console.log('🔌 STOMP Disconnected');

                    set({
                        isConnected: false,
                        isConnecting: false
                    });
                }
            });

            console.log('🚀 Activating STOMP client...');
            stompClient.activate();

        } catch (error) {
            console.error('❌ Connection error:', error);
            set({
                isConnected: false,
                isConnecting: false,
                connectionError: error.message
            });
            get().scheduleReconnect();
        }
    },


    // 알림 삭제
    removeNotification: (id) => set((state) => ({
        notifications: state.notifications.filter((n) => n.id !== id)
    })),

    // 웹소켓 연결 해제
    disconnect: () => {
        console.log('⛔ Disconnecting WebSocket...');

        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout);
            reconnectTimeout = null;
        }


        // STOMP 연결 해제
        if (stompClient) {
            try {
                stompClient.deactivate();
                console.log('✅ STOMP client deactivated');
            } catch (error) {
                console.error('❌ Error during disconnect:', error);
            }
            stompClient = null;
        }

        set({
            isConnected: false,
            isConnecting: false,
            reconnectAttempts: 0,
            connectionError: null
        });
    }
}));