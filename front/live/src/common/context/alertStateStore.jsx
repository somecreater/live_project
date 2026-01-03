import { create } from 'zustand';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { API_END_POINT } from '../api/Api';

let stompClient = null;
let pingInterval = null;

export const alertStateStore = create((set, get) => ({
    notifications: [],
    isConnected: false,
    isConnecting: false,

    // 알림 추가
    addNotification: (notification) => set((state) => ({
        notifications: [...state.notifications, { ...notification, id: notification.id || Date.now() + Math.random().toString(36).substring(2, 11) }]
    })),

    // 알림 목록 초기화
    clearNotifications: () => set({ notifications: [] }),

    // 웹소켓 연결
    connect: () => {
        // 이미 연결된 상태면 중복 연결 방지
        if (get().isConnected || get().isConnecting) return;

        set({ isConnecting: true });

        // 1. 인자로 받은 토큰이 없으면 쿠키에서 검색
        let token = null;
        const match = document.cookie.match(new RegExp('(^| )accessToken=([^;]+)'));
        if (match) token = match[2];

        if (token) {
            // URL 디코딩
            token = decodeURIComponent(token);
            // Bearer 접두사가 있는 경우 제거
            if (token.startsWith('Bearer ')) {
                token = token.substring(7);
            }
        }

        //${API_BASE_URL}/notify
        const socket = new SockJS(API_END_POINT.alert.alert_connect);
        stompClient = Stomp.over(socket);

        // 디버그 로그 끄기 (선택 사항)
        // stompClient.debug = null;

        const headers = token ? { Authorization: `Bearer ${token}` } : {};

        stompClient.connect(
            headers,
            (frame) => {
                console.log('✅ WebSocket Connected');
                set({ isConnected: true, isConnecting: false });

                if (pingInterval) clearInterval(pingInterval); // 기존 인터벌 제거 방어 코드

                pingInterval = setInterval(() => {
                    // 연결 상태 확인 후 전송
                    if (stompClient && stompClient.connected) {
                        // Spring Config의 prefix가 /app 이라고 가정 (/app/ping)
                        // body가 없으므로 {} 전달
                        try {
                            stompClient.send("/app/ping", {}, {});
                            console.log('💓 Sent Ping to Server');
                        } catch (e) {
                            console.error('Ping send failed', e);
                        }
                    }
                }, 45000);
                /**
                 * 2. 사용자별 알림 채널 구독
                 * 클라이언트에서는 '/user/queue/alerts'를 구독하면 
                 * 서버의 setUserDestinationPrefix("/user") 설정에 의해 본인의 메시지만 수신합니다.
                 */
                stompClient.subscribe(API_END_POINT.alert.alert_subscribe, (message) => {
                    if (message.body) {
                        const payload = JSON.parse(message.body);

                        // 헤더 정보 접근 (서버에서 설정한 nativeHeader들)
                        const sender = message.headers['sender'];
                        const priority = message.headers['priority'];

                        console.log(`신규 알림 [우선순위: ${priority}]:`, payload);

                        get().addNotification({
                            content: payload,
                            sender,
                            priority,
                            timestamp: new Date().toLocaleTimeString()
                        });
                    }
                });
            }, (error) => {
                console.error('❌ WebSocket Error/Disconnected:', error);

                if (pingInterval) clearInterval(pingInterval);

                set({ isConnected: false, isConnecting: false });
                stompClient = null;
                setTimeout(() => {
                    console.log('🔄 Reconnecting WebSocket...');
                    get().connect(); // 재귀 호출 (쿠키에서 토큰 다시 가져옴)
                }, 5000);
            });
    },

    // 알림 삭제
    removeNotification: (id) => set((state) => ({
        notifications: state.notifications.filter((n) => n.id !== id)
    })),

    // 웹소켓 연결 해제
    disconnect: () => {
        if (pingInterval) {
            clearInterval(pingInterval);
            pingInterval = null;
        }

        if (stompClient && stompClient.connected) {
            stompClient.disconnect(() => {
                console.log("⛔ Disconnected");
            });
        }
        stompClient = null;
        set({ isConnected: false, isConnecting: false });
    }
}));