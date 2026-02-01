import { useState } from "react";
import ApiService from "../../common/api/ApiService";

const API_CONFIG = {
    USER: {
        getList: ApiService.manager.get_user_list,
        delete: ApiService.manager.force_user_delete,
        listKey: 'user_list',
        idKey: 'userId',
    },
    CHANNEL: {
        getList: ApiService.manager.get_channel_list,
        delete: ApiService.manager.force_channel_delete,
        listKey: 'channel_list',
        idKey: 'channelId',
    },
    VIDEO: {
        getList: ApiService.manager.get_video_list,
        delete: ApiService.manager.force_video_delete,
        listKey: 'video_list',
        idKey: 'videoId',
    },
    POST: {
        getList: ApiService.manager.get_post_list,
        delete: ApiService.manager.force_post_delete,
        listKey: 'post_list',
        idKey: 'postId',
    },
    MESSAGE: {
        send: ApiService.manager.send_manager_message,
    }
}
function useManagerApi() {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    /**
       * 리소스 목록 조회
       * @param {string} resourceType - 'USER' | 'CHANNEL' | 'VIDEO' | 'POST'
       * @param {object} request - { page, size, type, keyword }
       * @returns {Promise<object|null>} - 페이지네이션 데이터 또는 null
       */
    const getList = async (resourceType, request) => {
        const config = API_CONFIG[resourceType];
        if (!config || !config.getList) {
            setError(`Invalid type: ${resourceType}`);
            return null;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await config.getList(request);
            const data = response.data;

            if (data.result) {
                const listData = data[config.listKey] || [];
                return {
                    content: listData.content || [],
                    page: listData.number || 0,
                    size: listData.size || 10,
                    totalPage: listData.totalPages || 0,
                    totalElements: listData.totalElements || 0,
                };
            } else {
                setError(data.message || 'Failed to fetch list');
                return null;
            }
        } catch (error) {
            setError(error.message);
            return null;
        } finally {
            setLoading(false);
        }
    }

    /**
       * 리소스 삭제
       * @param {string} type - 'USER' | 'CHANNEL' | 'VIDEO' | 'POST'
       * @param {string|number} id - 삭제할 리소스 ID
       * @returns {Promise<boolean>} - 성공 여부
       */
    const deleteResource = async (type, id) => {
        const config = API_CONFIG[type];
        if (!config || !config.delete) {
            setError(`Delete not supported for type: ${type}`);
            return false;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await config.delete(id);
            const data = response.data;

            if (data.result) {
                return true;
            } else {
                setError(data.message || 'Failed to delete resource');
                return false;
            }
        } catch (error) {
            setError(error.message);
            return false;
        } finally {
            setLoading(false);
        }
    }

    /**
       * 관리자 메시지 전송
       * @param {object} messageData - { title, content, targetId }
       * @returns {Promise<boolean>} - 성공 여부
       */
    const sendMessage = async (messageData) => {
        const config = API_CONFIG.MESSAGE;
        if (!config || !config.send) {
            setError('Message send not supported');
            return false;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await config.send({
                id: null,
                title: messageData.title,
                content: messageData.content,
                publisher: null,
                targetId: messageData.targetId,
                read: false,
                createdAt: null
            })
            const data = response.data;
            if (data.result) {
                return true;
            } else {
                setError(data.message || 'Failed to send message');
                return false;
            }
        } catch (error) {
            setError(error.message);
            return false;
        } finally {
            setLoading(false);
        }
    }

    return {
        getList,
        deleteResource,
        sendMessage,
        loading,
        error
    };
}

export default useManagerApi;