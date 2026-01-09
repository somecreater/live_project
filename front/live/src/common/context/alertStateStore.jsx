import { create } from 'zustand';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { API_END_POINT } from '../api/Api';
import { getAllAlerts, addAlert, deleteAlert, clearAllAlerts, markAllAlertsAsRead, deleteExcessAlerts, AlertEvent } from '../config/IndexedDB';
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

    // 알림 추가
    addNotification: async (notification) => {
        try {
            const alertEvent = new AlertEvent(
                notification.eventSubType || 'NORMAL',
                notification.publisher || 'System',
                notification.content,
                Date.now()
            );

            // IndexedDB에 저장
            await addAlert(alertEvent);

            // 개수 제한 (100개 유지)
            await deleteExcessAlerts(100);

            set((state) => ({
                notifications: [...state.notifications, {
                    ...alertEvent.toJSON(),
                    timestamp: new Date(alertEvent.timestamp).toISOString()
                }]
            }));
        } catch (error) {
            console.error('❌ Failed to add notification to IndexedDB:', error);
        }
    },

    // 알림 불러오기 (IndexedDB)
    loadNotifications: async () => {
        if (get().hasLoaded) return;

        try {
            const saved = await getAllAlerts({ orderBy: 'timestamp', order: 'asc' });
            set({
                notifications: saved.map(a => ({
                    ...a.toJSON(),
                    timestamp: new Date(a.timestamp).toISOString()
                })),
                hasLoaded: true
            });
            return saved;
        } catch (error) {
            console.error('❌ Failed to load notifications:', error);
            return [];
        }
    },

    // 서버에서 알림 목록 가져와서 동기화
    fetchNotifications: async () => {
        try {
            console.log('📡 서버에서 알림 목록 요청 중...');

            // 기존 IndexedDB에서 읽음 상태 맵 저장 (알림 ID -> 읽음 여부)
            const existingAlerts = await getAllAlerts({ orderBy: 'timestamp', order: 'asc' });
            const readStatusMap = new Map();
            existingAlerts.forEach(alert => {
                // id 또는 content+timestamp 조합을 키로 사용
                readStatusMap.set(alert.id, alert.read);
                // 서버 알림과 매칭을 위한 추가 키 (content + publisher + timestamp 근사치)
                const contentKey = `${alert.publisher}_${alert.content}_${Math.floor(alert.timestamp / 60000)}`;
                if (alert.read) {
                    readStatusMap.set(contentKey, true);
                }
            });

            const response = await ApiService.alert.get_list();
            const data = response.data;

            let serverAlerts = [];
            if (Array.isArray(data)) {
                serverAlerts = data;
            } else if (data && Array.isArray(data.content)) {
                serverAlerts = data.content;
            } else if (data && Array.isArray(data.alert_list)) {
                serverAlerts = data.alert_list;
            } else if (data && data.result && Array.isArray(data.alerts)) {
                serverAlerts = data.alerts;
            }

            console.log(`📥 서버 응답 수신: ${serverAlerts.length}개의 알림 발견`);

            // 기존 데이터(메모리 & IndexedDB) 초기화
            set({ notifications: [], hasLoaded: false });
            await clearAllAlerts();

            if (serverAlerts.length > 0) {
                const alertEvents = serverAlerts.map(alert => {
                    // 서버 데이터 구조에 맞춰 AlertEvent 생성
                    const event = new AlertEvent(
                        alert.eventSubType || alert.type || 'NORMAL',
                        alert.publisher || 'System',
                        alert.content || alert.message || '알림 내용 없음',
                        alert.timestamp ? new Date(alert.timestamp).getTime() : Date.now()
                    );

                    if (alert.id) event.id = alert.id;

                    if (alert.read !== undefined) {
                        event.read = alert.read;
                    } else {
                        const contentKey = `${event.publisher}_${event.content}_${Math.floor(event.timestamp / 60000)}`;
                        event.read = readStatusMap.get(alert.id) || readStatusMap.get(contentKey) || false;
                    }

                    return event;
                });

                // IndexedDB에 저장
                for (const event of alertEvents) {
                    await addAlert(event);
                }

                // 상태 업데이트
                set({
                    notifications: alertEvents.map(a => ({
                        ...a.toJSON(),
                        timestamp: new Date(a.timestamp).toISOString()
                    })),
                    hasLoaded: true
                });
                console.log('✅ 알림 상태 동기화 완료');
            } else {
                set({ hasLoaded: true });
                console.log('ℹ️ 가져올 알림이 없습니다.');
            }
        } catch (error) {
            console.error('❌ Failed to fetch notifications from server:', error);
            // 에러 시에도 최소한 로딩 완료 처리는 해서 무한 요청 방지
            set({ hasLoaded: true });
        }
    },

    // 모든 알림 읽음 처리
    markNotificationsAsRead: async () => {
        try {
            await markAllAlertsAsRead();
            set((state) => ({
                notifications: state.notifications.map(n => ({ ...n, read: true }))
            }));
        } catch (error) {
            console.error('❌ Failed to mark alerts as read:', error);
        }
    },

    // 알림 목록 초기화
    clearNotifications: async () => {
        try {
            await clearAllAlerts();
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
                            const publisher = message.headers['sender'] || 'System';
                            const priority = message.headers['priority'] || 'NORMAL';
                            const eventType = message.headers['eventType'] || 'UNKNOWN';
                            const eventSubType = message.headers['eventSubType'] || 'UNKNOWN';

                            console.log(`📬 신규 알림 [${eventType}/${eventSubType}][우선순위: ${priority}]:`, content);

                            get().addNotification({
                                content,
                                publisher,
                                priority,
                                eventType,
                                eventSubType,
                                timestamp: new Date().toISOString()
                            });
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


    // 알림 삭제
    removeNotification: async (id) => {
        try {
            await deleteAlert(id);
            set((state) => ({
                notifications: state.notifications.filter((n) => n.id !== id)
            }));
        } catch (error) {
            console.error('❌ Failed to delete alert:', error);
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

        console.log('✅ 알림 연결 해제 완료 (알림 데이터는 유지됨)');
    }
}));