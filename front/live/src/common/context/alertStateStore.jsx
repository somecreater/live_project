import { create } from 'zustand';
import ApiService from '../api/ApiService';
import { webSocketStateStore } from './webSocketStateStore';

export const alertStateStore = create((set, get) => ({
    notifications: [],
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    pageSize: 10,
    isConnected: false,
    isConnecting: false,
    connectionError: null,
    hasLoaded: false,
    isLoadingMore: false,

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
            notifications: [newNotification, ...state.notifications],
            totalElements: state.totalElements + 1
        }));
    },

    loadNotifications: async () => {
        return get().fetchNotifications(0, get().pageSize, true);
    },

    refetchNotifications: async () => {
        return get().fetchNotifications(0, get().pageSize, true);
    },

    fetchMoreNotifications: async () => {
        const { currentPage, totalPages, isLoadingMore } = get();
        if (isLoadingMore || currentPage >= totalPages - 1) return;

        set({ isLoadingMore: true });
        await get().fetchNotifications(currentPage + 1, get().pageSize);
        set({ isLoadingMore: false });
    },

    fetchNotifications: async (pageArg = 0, sizeArg = 10, forceArg = false) => {
        let page = typeof pageArg === 'number' ? pageArg : 0;
        let size = typeof sizeArg === 'number' ? sizeArg : 10;
        let force = forceArg;


        if (!force && get().hasLoaded && page === 0) return;

        try {
            const response = await ApiService.alert.get_list({ page, size });
            const data = response.data;

            let serverAlerts = [];
            let totalPages = 0;
            let totalElements = 0;

            if (data && data.result && data.alerts) {
                if (Array.isArray(data.alerts.content)) {
                    serverAlerts = data.alerts.content;
                    totalPages = data.alerts.totalPages || 0;
                    totalElements = data.alerts.totalElements || 0;
                } else if (Array.isArray(data.alerts)) {
                    serverAlerts = data.alerts;
                }
            }

            const mappedAlerts = serverAlerts.map(alert => {
                const rawId = alert.alertId || alert.id;
                let finalId;
                if (rawId) {
                    const parsed = parseInt(rawId, 10);
                    finalId = (!isNaN(parsed) && String(parsed) === String(rawId)) ? parsed : rawId;
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

            set((state) => ({
                notifications: page === 0 ? mappedAlerts : [...state.notifications, ...mappedAlerts],
                currentPage: page,
                totalPages: totalPages || state.totalPages,
                totalElements: totalElements || state.totalElements,
                hasLoaded: true
            }));
        } catch (error) {
            console.error("Failed to fetch notifications:", error);
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
        } catch (error) { }
    },

    markNotificationsAsRead: async () => {
        try {
            await ApiService.alert.get_read_all();
            set((state) => ({
                notifications: state.notifications.map(n => ({ ...n, read: true }))
            }));
        } catch (error) { }
    },

    removeNotification: async (id) => {
        try {
            await ApiService.alert.get_delete(id);
            set((state) => ({
                notifications: state.notifications.filter((n) => String(n.id) !== String(id))
            }));
        } catch (error) { }
    },

    clearNotifications: async () => {
        try {
            await ApiService.alert.get_delete_all();
            set({ notifications: [] });
        } catch (error) { }
    },

    connect: () => {
        webSocketStateStore.getState().connect((message) => {
            try {
                let parsedBody;
                try {
                    parsedBody = JSON.parse(message.body);
                } catch (e) {
                    parsedBody = message.body;
                }

                const headers = message.headers || {};
                if (headers['type'] != 'ALERT_EVENT') {
                    return;
                }

                const alertType = headers['eventSubType'] || headers['eventType'] || (parsedBody && parsedBody.type) || 'NORMAL';
                const sender = headers['sender'] || (parsedBody && (parsedBody.publisher || parsedBody.sender)) || 'System';
                const alertId = headers['alertId'] || headers['message-id'] || (parsedBody && parsedBody.id) || Date.now();
                const alertTime = headers['alertTime'] || (parsedBody && (parsedBody.timestamp || parsedBody.alertTime)) || new Date().toISOString();

                let content = '';
                if (typeof parsedBody === 'string') {
                    content = parsedBody;
                } else if (parsedBody && typeof parsedBody === 'object') {
                    content = parsedBody.content || parsedBody.message || message.body;
                } else {
                    content = message.body;
                }

                get().addNotification({
                    id: Number(alertId),
                    type: alertType,
                    publisher: sender,
                    content: content,
                    read: (parsedBody && parsedBody.read) || false,
                    timestamp: alertTime
                });
            } catch (error) { }
        });
    },

    disconnect: () => {
        webSocketStateStore.getState().disconnect();
        set({
            notifications: [],
            currentPage: 0,
            totalPages: 0,
            totalElements: 0,
            hasLoaded: false,
            isConnected: false,
            isConnecting: false,
            connectionError: null
        });
    }
}));

webSocketStateStore.subscribe((state) => {
    alertStateStore.setState({
        isConnected: state.isConnected,
        isConnecting: state.isConnecting,
        connectionError: state.connectionError
    });
});