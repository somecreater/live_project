import { openDB } from 'idb';

// DB 설정
const DB_NAME = "alertDB";
const STORE_NAME = "alerts";
const DB_VERSION = 1;

/**
 * AlertType Enum
 * Java의 AlertType enum을 JavaScript 객체로 변환
 */
export const AlertType = {
    USER_UPDATE: {
        name: 'USER_UPDATE',
        type: 'USER',
        subtype: 'USER_UPDATE',
        priority: 'HIGH'
    },
    VIDEO_UPLOAD: {
        name: 'VIDEO_UPLOAD',
        type: 'CHANNEL',
        subtype: 'VIDEO_UPLOAD',
        priority: 'NORMAL'
    },
    STREAMING_START: {
        name: 'STREAMING_START',
        type: 'CHANNEL',
        subtype: 'STREAMING_START',
        priority: 'HIGH'
    },
    STREAMING_STOP: {
        name: 'STREAMING_STOP',
        type: 'CHANNEL',
        subtype: 'STREAMING_STOP',
        priority: 'HIGH'
    },
    POST_UPLOAD: {
        name: 'POST_UPLOAD',
        type: 'CHANNEL',
        subtype: 'POST_UPLOAD',
        priority: 'NORMAL'
    },
    POST_UPDATE: {
        name: 'POST_UPDATE',
        type: 'CHANNEL',
        subtype: 'POST_UPDATE',
        priority: 'NORMAL'
    },
    POST_DELETE: {
        name: 'POST_DELETE',
        type: 'CHANNEL',
        subtype: 'POST_DELETE',
        priority: 'NORMAL'
    },
    REPLY_UPLOAD: {
        name: 'REPLY_UPLOAD',
        type: 'REPLY',
        subtype: 'REPLY_UPLOAD',
        priority: 'NORMAL'
    },
    CHANNEL_UPDATE: {
        name: 'CHANNEL_UPDATE',
        type: 'CHANNEL',
        subtype: 'CHANNEL_UPDATE',
        priority: 'HIGH'
    },
    CHANNEL_DELETE: {
        name: 'CHANNEL_DELETE',
        type: 'CHANNEL',
        subtype: 'CHANNEL_DELETE',
        priority: 'HIGH'
    }
};

export class AlertEvent {
    constructor(type, publisher, content, timestamp = Date.now()) {
        // type이 문자열인 경우 AlertType에서 찾기
        if (typeof type === 'string') {
            this.type = AlertType[type];
            if (!this.type) {
                throw new Error(`Invalid alert type: ${type}`);
            }
        } else {
            this.type = type;
        }

        this.publisher = publisher;
        this.content = content;
        this.timestamp = timestamp;
        this.id = `${timestamp}_${Math.random().toString(36).substr(2, 9)}`; // 고유 ID 생성
        this.read = false; // 읽음 여부
    }

    // Getter 메서드들
    getType() {
        return this.type;
    }

    getPublisher() {
        return this.publisher;
    }

    getContent() {
        return this.content;
    }

    getTimestamp() {
        return this.timestamp;
    }

    getId() {
        return this.id;
    }

    isRead() {
        return this.read;
    }

    markAsRead() {
        this.read = true;
    }

    // 직렬화 (IndexedDB 저장용)
    toJSON() {
        return {
            id: this.id,
            type: this.type.name,
            publisher: this.publisher,
            content: this.content,
            timestamp: this.timestamp,
            read: this.read
        };
    }

    // 역직렬화 (IndexedDB에서 읽어올 때)
    static fromJSON(json) {
        const event = new AlertEvent(json.type, json.publisher, json.content, json.timestamp);
        event.id = json.id;
        event.read = json.read;
        return event;
    }
}

/**
 * IndexedDB 초기화
 */
const initDB = async () => {
    return await openDB(DB_NAME, DB_VERSION, {
        upgrade(db) {
            // Object Store가 없으면 생성
            if (!db.objectStoreNames.contains(STORE_NAME)) {
                const store = db.createObjectStore(STORE_NAME, {
                    keyPath: 'id'
                });

                // 인덱스 생성
                store.createIndex('timestamp', 'timestamp', { unique: false });
                store.createIndex('type', 'type', { unique: false });
                store.createIndex('publisher', 'publisher', { unique: false });
                store.createIndex('read', 'read', { unique: false });
                store.createIndex('priority', 'priority', { unique: false });
            }
        },
    });
};

/**
 * 알림 추가
 * @param {AlertEvent} alertEvent - 추가할 알림 이벤트
 * @returns {Promise<string>} 추가된 알림의 ID
 */
export const addAlert = async (alertEvent) => {
    try {
        const db = await initDB();
        const data = alertEvent.toJSON();

        // priority 정보도 함께 저장 (인덱싱용)
        data.priority = alertEvent.type.priority;

        await db.add(STORE_NAME, data);
        return data.id;
    } catch (error) {
        console.error('Error adding alert:', error);
        throw error;
    }
};

/**
 * 모든 알림 조회
 * @param {Object} options - 조회 옵션
 * @param {string} options.orderBy - 정렬 기준 ('timestamp', 'priority' 등)
 * @param {string} options.order - 정렬 순서 ('asc', 'desc')
 * @param {number} options.limit - 조회 개수 제한
 * @returns {Promise<AlertEvent[]>} 알림 이벤트 배열
 */
export const getAllAlerts = async (options = {}) => {
    try {
        const db = await initDB();
        const { orderBy = 'timestamp', order = 'desc', limit } = options;

        let alerts = await db.getAll(STORE_NAME);

        // AlertEvent 객체로 변환
        alerts = alerts.map(data => AlertEvent.fromJSON(data));

        // 정렬
        if (orderBy === 'timestamp') {
            alerts.sort((a, b) => {
                return order === 'desc'
                    ? b.timestamp - a.timestamp
                    : a.timestamp - b.timestamp;
            });
        } else if (orderBy === 'priority') {
            const priorityOrder = { 'HIGH': 0, 'NORMAL': 1, 'LOW': 2 };
            alerts.sort((a, b) => {
                const priorityA = priorityOrder[a.type.priority] || 2;
                const priorityB = priorityOrder[b.type.priority] || 2;
                return order === 'desc'
                    ? priorityA - priorityB
                    : priorityB - priorityA;
            });
        }

        // 개수 제한
        if (limit && limit > 0) {
            alerts = alerts.slice(0, limit);
        }

        return alerts;
    } catch (error) {
        console.error('Error getting all alerts:', error);
        throw error;
    }
};

/**
 * ID로 알림 조회
 * @param {string} id - 알림 ID
 * @returns {Promise<AlertEvent|null>} 알림 이벤트 또는 null
 */
export const getAlertById = async (id) => {
    try {
        const db = await initDB();
        const data = await db.get(STORE_NAME, id);
        return data ? AlertEvent.fromJSON(data) : null;
    } catch (error) {
        console.error('Error getting alert by id:', error);
        throw error;
    }
};

/**
 * 특정 조건으로 알림 조회
 * @param {Object} filter - 필터 조건
 * @param {string} filter.type - 알림 타입
 * @param {string} filter.publisher - 발행자
 * @param {boolean} filter.read - 읽음 여부
 * @returns {Promise<AlertEvent[]>} 필터링된 알림 이벤트 배열
 */
export const getAlertsByFilter = async (filter = {}) => {
    try {
        const db = await initDB();
        let alerts = await db.getAll(STORE_NAME);

        // 필터링
        if (filter.type) {
            alerts = alerts.filter(alert => alert.type === filter.type);
        }
        if (filter.publisher) {
            alerts = alerts.filter(alert => alert.publisher === filter.publisher);
        }
        if (filter.read !== undefined) {
            alerts = alerts.filter(alert => alert.read === filter.read);
        }

        // AlertEvent 객체로 변환
        return alerts.map(data => AlertEvent.fromJSON(data));
    } catch (error) {
        console.error('Error getting alerts by filter:', error);
        throw error;
    }
};

/**
 * 읽지 않은 알림 조회
 * @returns {Promise<AlertEvent[]>} 읽지 않은 알림 이벤트 배열
 */
export const getUnreadAlerts = async () => {
    return await getAlertsByFilter({ read: false });
};

/**
 * 알림을 읽음으로 표시
 * @param {string} id - 알림 ID
 * @returns {Promise<void>}
 */
export const markAlertAsRead = async (id) => {
    try {
        const db = await initDB();
        const data = await db.get(STORE_NAME, id);
        if (data) {
            data.read = true;
            await db.put(STORE_NAME, data);
        }
    } catch (error) {
        console.error('Error marking alert as read:', error);
        throw error;
    }
};

/**
 * 여러 알림을 읽음으로 표시
 * @param {string[]} ids - 알림 ID 배열
 * @returns {Promise<void>}
 */
export const markAlertsAsRead = async (ids) => {
    try {
        const db = await initDB();
        const tx = db.transaction(STORE_NAME, 'readwrite');

        await Promise.all(
            ids.map(async (id) => {
                const data = await tx.store.get(id);
                if (data) {
                    data.read = true;
                    await tx.store.put(data);
                }
            })
        );

        await tx.done;
    } catch (error) {
        console.error('Error marking alerts as read:', error);
        throw error;
    }
};

/**
 * 모든 알림을 읽음으로 표시
 * @returns {Promise<void>}
 */
export const markAllAlertsAsRead = async () => {
    try {
        const db = await initDB();
        const alerts = await db.getAll(STORE_NAME);
        const tx = db.transaction(STORE_NAME, 'readwrite');

        await Promise.all(
            alerts.map(async (alert) => {
                alert.read = true;
                await tx.store.put(alert);
            })
        );

        await tx.done;
    } catch (error) {
        console.error('Error marking all alerts as read:', error);
        throw error;
    }
};

/**
 * 알림 삭제
 * @param {string} id - 알림 ID
 * @returns {Promise<void>}
 */
export const deleteAlert = async (id) => {
    try {
        const db = await initDB();
        await db.delete(STORE_NAME, id);
    } catch (error) {
        console.error('Error deleting alert:', error);
        throw error;
    }
};

/**
 * 여러 알림 삭제
 * @param {string[]} ids - 알림 ID 배열
 * @returns {Promise<void>}
 */
export const deleteAlerts = async (ids) => {
    try {
        const db = await initDB();
        const tx = db.transaction(STORE_NAME, 'readwrite');

        await Promise.all(
            ids.map(async (id) => {
                await tx.store.delete(id);
            })
        );

        await tx.done;
    } catch (error) {
        console.error('Error deleting alerts:', error);
        throw error;
    }
};

/**
 * 모든 알림 삭제
 * @returns {Promise<void>}
 */
export const clearAllAlerts = async () => {
    try {
        const db = await initDB();
        await db.clear(STORE_NAME);
    } catch (error) {
        console.error('Error clearing all alerts:', error);
        throw error;
    }
};

/**
 * 오래된 알림 삭제 (특정 기간 이전의 알림)
 * @param {number} days - 삭제할 기준 일수 (기본값: 30일)
 * @returns {Promise<number>} 삭제된 알림 개수
 */
export const deleteOldAlerts = async (days = 30) => {
    try {
        const db = await initDB();
        const cutoffTime = Date.now() - (days * 24 * 60 * 60 * 1000);

        const alerts = await db.getAll(STORE_NAME);
        const oldAlerts = alerts.filter(alert => alert.timestamp < cutoffTime);

        const tx = db.transaction(STORE_NAME, 'readwrite');
        await Promise.all(
            oldAlerts.map(async (alert) => {
                await tx.store.delete(alert.id);
            })
        );
        await tx.done;

        return oldAlerts.length;
    } catch (error) {
        console.error('Error deleting old alerts:', error);
        throw error;
    }
};

/**
 * 알림 개수 제한 유지 (오래된 것부터 삭제)
 * @param {number} maxCount - 유지할 최대 알림 개수 (기본값: 100)
 * @returns {Promise<number>} 삭제된 알림 개수
 */
export const deleteExcessAlerts = async (maxCount = 100) => {
    try {
        const db = await initDB();
        const allAlerts = await db.getAllFromIndex(STORE_NAME, 'timestamp');

        if (allAlerts.length <= maxCount) return 0;

        const deleteCount = allAlerts.length - maxCount;
        const toDelete = allAlerts.slice(0, deleteCount);

        const tx = db.transaction(STORE_NAME, 'readwrite');
        await Promise.all(
            toDelete.map(alert => tx.store.delete(alert.id))
        );
        await tx.done;

        return deleteCount;
    } catch (error) {
        console.error('Error deleting excess alerts:', error);
        throw error;
    }
};

/**
 * 알림 개수 조회
 * @param {Object} filter - 필터 조건
 * @param {boolean} filter.read - 읽음 여부
 * @returns {Promise<number>} 알림 개수
 */
export const getAlertCount = async (filter = {}) => {
    try {
        const db = await initDB();
        let alerts = await db.getAll(STORE_NAME);

        if (filter.read !== undefined) {
            alerts = alerts.filter(alert => alert.read === filter.read);
        }

        return alerts.length;
    } catch (error) {
        console.error('Error getting alert count:', error);
        throw error;
    }
};

/**
 * 읽지 않은 알림 개수 조회
 * @returns {Promise<number>} 읽지 않은 알림 개수
 */
export const getUnreadAlertCount = async () => {
    return await getAlertCount({ read: false });
};

// 기본 내보내기
export default {
    AlertType,
    AlertEvent,
    addAlert,
    getAllAlerts,
    getAlertById,
    getAlertsByFilter,
    getUnreadAlerts,
    markAlertAsRead,
    markAlertsAsRead,
    markAllAlertsAsRead,
    deleteAlert,
    deleteAlerts,
    clearAllAlerts,
    deleteOldAlerts,
    deleteExcessAlerts,
    getAlertCount,
    getUnreadAlertCount
};
