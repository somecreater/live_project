import { create } from 'zustand';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { API_END_POINT } from '../api/Api';

let stompClient = null;
let reconnectTimeout = null;
let isReconnecting = false;

export const webSocketStateStore = create((set, get) => ({
    isConnected: false,
    isConnecting: false,
    reconnectAttempts: 0,
    maxReconnectAttempts: 5,
    connectionError: null,

    //웹소켓 연결
    connect: (onMessageReceived) => {
        const state = get();
        if (state.isConnected || state.isConnecting) return;

        set({ isConnecting: true, connectionError: null });

        try {
            if (stompClient) {
                try {
                    stompClient.deactivate();
                } catch (e) { }
                stompClient = null;
            }

            stompClient = new Client({
                webSocketFactory: () => new SockJS(API_END_POINT.alert.alert_connect, null, {
                    withCredentials: true
                }),
                reconnectDelay: 0,
                heartbeatIncoming: 10000,
                heartbeatOutgoing: 10000,

                onConnect: () => {
                    set({
                        isConnected: true,
                        isConnecting: false,
                        reconnectAttempts: 0,
                        connectionError: null
                    });
                    isReconnecting = false;

                    stompClient.subscribe(API_END_POINT.alert.alert_subscribe, (message) => {
                        if (onMessageReceived) onMessageReceived(message);
                    });
                },
                debug: (str) => {
                    console.log('STOMP:', str);
                },

                onStompError: (frame) => {
                    set({
                        isConnected: false,
                        isConnecting: false,
                        connectionError: frame.headers['message'] || 'STOMP error'
                    });
                    if (frame.headers['message']?.includes('Authentication') ||
                        frame.headers['message']?.includes('Authorization')) return;
                    get().scheduleReconnect(onMessageReceived);
                },

                onWebSocketClose: (event) => {
                    set({ isConnected: false, isConnecting: false });
                    if (event.code !== 1000) get().scheduleReconnect(onMessageReceived);
                },

                onDisconnect: () => {
                    set({ isConnected: false, isConnecting: false });
                }
            });

            stompClient.activate();
        } catch (error) {
            set({ isConnected: false, isConnecting: false, connectionError: error.message });
            get().scheduleReconnect(onMessageReceived);
        }
    },

    scheduleReconnect: (onMessageReceived) => {
        const state = get();
        if (isReconnecting || state.reconnectAttempts >= state.maxReconnectAttempts) return;

        isReconnecting = true;
        const delay = Math.min(1000 * Math.pow(2, state.reconnectAttempts), 30000);

        if (reconnectTimeout) clearTimeout(reconnectTimeout);
        reconnectTimeout = setTimeout(() => {
            set({ reconnectAttempts: state.reconnectAttempts + 1 });
            isReconnecting = false;
            get().connect(onMessageReceived);
        }, delay);
    },

    disconnect: () => {
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout);
            reconnectTimeout = null;
        }

        if (stompClient) {
            try {
                stompClient.deactivate();
            } catch (error) { }
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