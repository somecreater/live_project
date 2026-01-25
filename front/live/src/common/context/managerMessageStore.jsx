import { create } from "zustand";
import ApiService from '../api/ApiService';
import { webSocketStateStore } from './webSocketStateStore';

export const managerMessageStore = create((set, get) => ({
    managermessages: [],
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    pageSize: 10,
    isConnected: false,
    isConnecting: false,
    connectionError: null,
    hasLoaded: false,
    isLoadingMore: false,

    addManagerMessage: (message) => {
        const newMessage = {
            id: message.id,
            title: message.title,
            content: message.content,
            publisher: message.publisher || 'SYSTEM',
            receiver: message.targetId,
            read: message.read || false,
            timestamp: message.timestamp || new Date().toISOString(),
        }

        set((state) => ({
            managermessages: [newMessage, ...state.managermessages],
            totalElements: state.totalElements + 1
        }));
    },

    loadMoreManagerMessages: async () => {
        const { currentPage, totalPages, isLoadingMore, pageSize } = get();
        if (isLoadingMore || currentPage >= totalPages - 1) return;
        set({ isLoadingMore: true });
        await get().loadManagerMessages(currentPage + 1, pageSize);
        set({ isLoadingMore: false });
    },

    loadManagerMessages: async (pageArg = 0, sizeArg = 10, forceArg = false) => {
        if (!forceArg && get().hasLoaded && pageArg === 0) return;

        try {
            const response = await ApiService.manager_message.get_list({ page: pageArg, size: sizeArg });
            const data = response.data;
            if (data && data.result && data.manager_messages) {
                const messagesData = data.manager_messages;
                let content = [];
                let totalElements = 0;
                let totalPages = 0;

                if (Array.isArray(messagesData.content)) {
                    content = messagesData.content;
                    totalElements = messagesData.totalElements;
                    totalPages = messagesData.totalPages;
                } else if (Array.isArray(messagesData)) {
                    content = messagesData;
                }

                const managerMessages = content.map((message) => ({
                    id: message.id,
                    title: message.title,
                    content: message.content,
                    publisher: message.publisher || 'SYSTEM',
                    receiver: message.targetId,
                    read: message.read || false,
                    timestamp: message.timestamp || new Date().toISOString(),
                }));

                set((state) => ({
                    managermessages: pageArg === 0 ? managerMessages : [...state.managermessages, ...managerMessages],
                    currentPage: pageArg,
                    totalPages: totalPages || state.totalPages,
                    totalElements: totalElements || state.totalElements,
                    hasLoaded: true,
                }));
            }
        } catch (error) {
            console.error("Failed to fetch manager messages:", error);
        }
    },

    managerMessageRead: async (id) => {
        try {
            const response = await ApiService.manager_message.read_message(id);
            const data = response.data;
            if (data && data.result) {
                set((state) => ({
                    managermessages: state.managermessages.map(message =>
                        message.id === id ? { ...message, read: true } : message
                    )
                }));
            }
        } catch (error) {
            console.error("Failed to read manager message:", error);
        }
    },

    managerMessageReadAll: async () => {
        try {
            const response = await ApiService.manager_message.read_message_all();
            const data = response.data;
            if (data && data.result) {
                set((state) => ({
                    managermessages: state.managermessages.map(message => ({ ...message, read: true }))
                }));
            }
        } catch (error) {
            console.error("Failed to read all manager messages:", error);
        }
    },

    managerMessageDelete: async (id) => {
        try {
            const response = await ApiService.manager_message.delete_message(id);
            const data = response.data;
            if (data && data.result) {
                set((state) => ({
                    managermessages: state.managermessages.filter(message => message.id !== id),
                    totalElements: Math.max(0, state.totalElements - 1)
                }));
            }
        } catch (error) {
            console.error("Failed to delete manager message:", error);
        }
    },

    managerMessageDeleteAll: async () => {
        try {
            const response = await ApiService.manager_message.delete_message_all();
            const data = response.data;
            if (data && data.result) {
                set({
                    managermessages: [],
                    totalElements: 0,
                    totalPages: 0,
                    currentPage: 0
                });
            }
        } catch (error) {
            console.error("Failed to delete all manager messages:", error);
        }
    },

    connect: () => {
        webSocketStateStore.getState().connect(
            (message) => {
                try {
                    let parsedBody;
                    try {
                        parsedBody = JSON.parse(message.body);
                    } catch (e) {
                        parsedBody = message.body;
                    }

                    const headers = message.headers || {};
                    if (headers['eventType'] !== 'MANAGER_MESSAGE') {
                        return;
                    }

                    let content_body = '';
                    if (parsedBody && typeof parsedBody === 'object') {
                        content_body = parsedBody.content || parsedBody.message || '';
                    } else if (typeof parsedBody === 'string') {
                        content_body = parsedBody;
                    } else {
                        content_body = headers['content'] || '';
                    }

                    const new_manager_message = {
                        id: headers['alertId'] || Date.now(),
                        title: headers['title'] || '관리자 알림',
                        content: content_body,
                        publisher: headers['publisher'] || 'SYSTEM',
                        receiver: headers['targetId'],
                        read: false,
                        timestamp: headers['alertTime'] || new Date().toISOString(),
                    };

                    get().addManagerMessage(new_manager_message);
                } catch (error) {
                    console.error("Failed to add manager message:", error);
                }
            }
        );
    },

    disconnect: () => {
        webSocketStateStore.getState().disconnect();
        set({
            managermessages: [],
            currentPage: 0,
            totalPages: 0,
            totalElements: 0,
            hasLoaded: false,
            isConnected: false,
            isConnecting: false,
            connectionError: null
        });
    },
}))

webSocketStateStore.subscribe((state) => {
    managerMessageStore.setState({
        isConnected: state.isConnected,
        isConnecting: state.isConnecting,
        connectionError: state.connectionError
    });
});
