import { create } from 'zustand';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { API_END_POINT } from '../api/Api';
import ApiService from '../api/ApiService';

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
    hasLoaded: false,

    // 알림 추가 (웹소켓으로 받은 실시간 알림)
    addNotification: (notification) => {
        const newNotification = {
            id: notification.id || Date.now(),
            type: notification.type || 'NORMAL',
            publisher: notification.publisher || 'System',
            content: notification.content || '알림이 도착했습니다.',
            read: notification.read || false,
            timestamp: notification.timestamp || new Date().toISOString()
        };

        set((state) => ({
            notifications: [...state.notifications, newNotification]
        }));
    },

    // 알림 불러오기 (서버 API 호출)
    loadNotifications: async () => {
        return get().fetchNotifications();
    },

    // 알림 강제 새로고침 (hasLoaded 무시)
    refetchNotifications: async () => {
        set({ hasLoaded: false });
        return get().fetchNotifications();
    },

    // 서버에서 알림 목록 가져오기
    fetchNotifications: async (force = false) => {
        if (!force && get().hasLoaded) return;

        try {
            console.log('📡 서버에서 알림 목록 요청 중...');

            const response = await ApiService.alert.get_list();
            const data = response.data;

            let serverAlerts = [];
            if (Array.isArray(data)) {
                serverAlerts = data;
            } else if (data && Array.isArray(data.data)) {
                serverAlerts = data.data;
            } else if (data && Array.isArray(data.content)) {
                serverAlerts = data.content;
            } else if (data && Array.isArray(data.alert_list)) {
                serverAlerts = data.alert_list;
            } else if (data && data.result && Array.isArray(data.alerts)) {
                serverAlerts = data.alerts;
            }

            console.log(`📥 서버 응답 수신: ${serverAlerts.length}개의 알림 발견`);

            const mappedAlerts = serverAlerts.map(alert => {
                const rawId = alert.alertId || alert.id;
                let finalId;
                if (rawId) {
                    const parsed = parseInt(rawId, 10);
                    // 유효한 숫자이고, 문자열과 일치할 경우에만 숫자로 사용
                    if (!isNaN(parsed) && String(parsed) === String(rawId)) {
                        finalId = parsed;
                    } else {
                        finalId = rawId;
                    }
                } else {
                    finalId = Date.now() + Math.random();
                }

                return {
                    id: finalId,
                    type: alert.type || 'NORMAL',
                    publisher: alert.publisher || alert.sender || 'System',
                    content: alert.content || alert.message || '알림 내용 없음',
                    read: alert.read !== undefined ? alert.read : (alert.isRead || false),
                    timestamp: alert.alertTime || alert.timestamp || alert.createdDate || alert.createdAt || new Date().toISOString()
                };
            });

            set({
                notifications: mappedAlerts,
                hasLoaded: true
            });

            console.log('✅ 알림 상태 동기화 완료');
        } catch (error) {
            console.error('❌ Failed to fetch notifications from server:', error);
            set({ hasLoaded: true });
        }
    },

    markNotificationAsRead: async (id) => {
        try {
            await ApiService.alert.get_read(id);
            set((state) => ({
                notifications: state.notifications.map(n =>
                    String(n.id) === String(id) ? { ...n, read: true } : n
                )
            }));
        } catch (error) {
            console.error('❌ Failed to mark alert as read:', error);
        }
    },

    // 모든 알림 읽음 처리
    markNotificationsAsRead: async () => {
        try {
            await ApiService.alert.get_read_all();
            set((state) => ({
                notifications: state.notifications.map(n => ({ ...n, read: true }))
            }));
        } catch (error) {
            console.error('❌ Failed to mark all alerts as read:', error);
        }
    },

    // 특정 알림 삭제
    removeNotification: async (id) => {
        try {
            await ApiService.alert.get_delete(id);
            set((state) => ({
                notifications: state.notifications.filter((n) => String(n.id) !== String(id))
            }));
        } catch (error) {
            console.error('❌ Failed to delete alert:', error);
        }
    },

    // 알림 목록 초기화 (모든 알림 삭제)
    clearNotifications: async () => {
        try {
            await ApiService.alert.get_delete_all();
            set({ notifications: [] });
        } catch (error) {
            console.error('❌ Failed to clear notifications:', error);
        }
    },

    // 재연결 로직
    scheduleReconnect: () => {
        const state = get();

        if (isReconnecting) {
            console.log('🔄 Already reconnecting...');
            return;
        }

        if (state.reconnectAttempts >= state.maxReconnectAttempts) {
            set({
                reconnectAttempts: 0,
                connectionError: 'Maximum reconnection attempts reached'
            });
            isReconnecting = false;
            return;
        }

        isReconnecting = true;
        const delay = Math.min(1000 * Math.pow(2, state.reconnectAttempts), 30000); // 최대 30초

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
                    return new SockJS(API_END_POINT.alert.alert_connect, null, {
                        withCredentials: true
                    });
                },
                debug: (str) => {
                    // console.log('STOMP:', str);
                },
                reconnectDelay: 0, // 자동 재연결 비활성화 (직접 관리)
                heartbeatIncoming: 10000,
                heartbeatOutgoing: 10000,
                onConnect: (frame) => {
                    console.log('✅ WebSocket Connected Successfully');
                    set({
                        isConnected: true,
                        isConnecting: false,
                        reconnectAttempts: 0,
                        connectionError: null
                    });
                    isReconnecting = false;


                    const subscriptionPath = API_END_POINT.alert.alert_subscribe;

                    // 알림 구독
                    stompClient.subscribe(subscriptionPath, (message) => {
                        try {
                            let parsedData = null;

                            // JSON 파싱 시도
                            try {
                                parsedData = JSON.parse(message.body);
                            } catch (e) {
                                parsedData = { content: message.body };
                            }

                            // 헤더에서 alertId 및 alertTime 추출
                            const alertIdHeader = message.headers['alertId'];
                            const alertTimeHeader = message.headers['alertTime'];

                            let notificationId;
                            if (alertIdHeader) {
                                const parsedId = Number(alertIdHeader);
                                if (!isNaN(parsedId) && String(parsedId) === alertIdHeader) {
                                    notificationId = parsedId;
                                } else {
                                    notificationId = alertIdHeader;
                                }
                            } else {
                                notificationId = parsedData.id || Date.now();
                            }

                            const notification = {
                                id: notificationId,
                                type: parsedData.type || 'NORMAL',
                                publisher: parsedData.publisher || 'System',
                                content: parsedData.content || message.body,
                                read: parsedData.read || false,
                                timestamp: alertTimeHeader || parsedData.timestamp || new Date().toISOString()
                            };

                            console.log(`📬 신규 알림 [ID: ${notification.id}][${notification.type}]:`, notification.content);

                            get().addNotification(notification);
                        } catch (error) {
                            console.error('❌ Error processing notification:', error);
                        }
                    });
                },

                onStompError: (frame) => {
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

    // 웹소켓 연결 해제
    disconnect: () => {
        console.log('🔌 알림 시스템 연결 해제 중...');

        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout);
            reconnectTimeout = null;
        }

        // STOMP 연결 해제
        if (stompClient) {
            try {
                stompClient.deactivate();
            } catch (error) {
                console.error('❌ Error during disconnect:', error);
            }
            stompClient = null;
        }

        set({
            notifications: [],
            hasLoaded: false,
            isConnected: false,
            isConnecting: false,
            reconnectAttempts: 0,
            connectionError: null
        });

        console.log('✅ 알림 연결 해제 완료');
    }
}));