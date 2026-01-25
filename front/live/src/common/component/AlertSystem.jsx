import React, { useEffect, useCallback, useRef, useState } from 'react';
import { alertStateStore } from '../context/alertStateStore';
import { userStateStore } from '../context/userStateStore';
import './AlertSystem.css';
import {
    FaBell, FaTimes, FaCheckCircle, FaVideo,
    FaBroadcastTower, FaEdit, FaTrash, FaComment, FaUser
} from 'react-icons/fa';

const AlertSystem = () => {
    const notifications = alertStateStore((state) => state.notifications);
    const connect = alertStateStore((state) => state.connect);
    const disconnect = alertStateStore((state) => state.disconnect);
    const refetchNotifications = alertStateStore((state) => state.refetchNotifications);
    const isAuthenticated = userStateStore((state) => state.isAuthenticated);

    // 연결 상태 추적
    const hasInitialized = useRef(false);
    const connectAttempted = useRef(false);
    const mountTime = useRef(Date.now());
    const [activeToasts, setActiveToasts] = useState([]);
    const processedIds = useRef(new Set());

    // 인증 상태에 따른 웹소켓 연결 관리
    useEffect(() => {
        if (!hasInitialized.current && isAuthenticated) {
            hasInitialized.current = true;
            connectAttempted.current = true;

            // 서버에서 저장된 알림을 가져온 후 웹소켓 연결
            refetchNotifications().then(() => {
                connect();
            });
        }

        if (!isAuthenticated && hasInitialized.current) {
            disconnect();
            hasInitialized.current = false;
            connectAttempted.current = false;
            // 상태 초기화
            processedIds.current.clear();
            setActiveToasts([]);
            mountTime.current = Date.now();
        }
    }, [isAuthenticated, connect, disconnect, refetchNotifications]);

    // 신규 알림 감지하여 토스트에 추가
    useEffect(() => {
        const newNotifications = notifications.filter(n => {
            // 마운트 시점 이후 & 아직 토스트로 처리되지 않은 알림만 필터링
            const timestamp = n.timestamp ? new Date(n.timestamp).getTime() : Date.now();
            return timestamp > mountTime.current && !processedIds.current.has(n.id);
        });

        if (newNotifications.length > 0) {
            newNotifications.forEach(n => processedIds.current.add(n.id));
            setActiveToasts(prev => [...prev, ...newNotifications]);
        }
    }, [notifications]);

    const removeToast = useCallback((id) => {
        setActiveToasts(prev => prev.filter(t => t.id !== id));
    }, []);

    return (
        <div className="alert-container">
            {activeToasts.map((notification) => (
                <AlertItem
                    key={notification.id}
                    notification={notification}
                    onRemove={removeToast}
                />
            ))}
        </div>
    );
};

const AlertItem = ({ notification, onRemove }) => {
    const { id, content, type, timestamp, publisher } = notification;
    const timerRef = useRef(null);

    useEffect(() => {
        // 5초 후 자동 삭제
        timerRef.current = setTimeout(() => {
            onRemove(id);
        }, 5000);

        return () => {
            if (timerRef.current) {
                clearTimeout(timerRef.current);
            }
        };
    }, [id, onRemove]);

    // AlertType enum에 따른 아이콘 결정
    const getIcon = useCallback((alertType) => {
        switch (alertType) {
            case 'VIDEO_UPLOAD':
                return <FaVideo />;
            case 'STREAMING_START':
            case 'STREAMING_STOP':
                return <FaBroadcastTower />;
            case 'POST_UPLOAD':
            case 'POST_UPDATE':
                return <FaEdit />;
            case 'POST_DELETE':
            case 'CHANNEL_DELETE':
                return <FaTrash />;
            case 'REPLY_UPLOAD':
                return <FaComment />;
            case 'USER_UPDATE':
                return <FaUser />;
            case 'CHANNEL_UPDATE':
                return <FaCheckCircle />;
            default:
                return <FaBell />;
        }
    }, []);

    // AlertType enum의 priority 값에 따른 우선순위 클래스 결정
    // HIGH: USER_UPDATE, STREAMING_START, STREAMING_STOP, CHANNEL_UPDATE, CHANNEL_DELETE
    // NORMAL: VIDEO_UPLOAD, POST_UPLOAD, POST_UPDATE, POST_DELETE, REPLY_UPLOAD
    const getPriorityClass = useCallback((alertType) => {
        const highPriorityTypes = [
            'USER_UPDATE',
            'STREAMING_START',
            'STREAMING_STOP',
            'CHANNEL_UPDATE',
            'CHANNEL_DELETE'
        ];
        const normalPriorityTypes = [
            'VIDEO_UPLOAD',
            'POST_UPLOAD',
            'POST_UPDATE',
            'POST_DELETE',
            'REPLY_UPLOAD'
        ];

        if (highPriorityTypes.includes(alertType)) {
            return 'high';
        } else if (normalPriorityTypes.includes(alertType)) {
            return 'normal';
        }
        return 'low';
    }, []);

    const renderMessage = useCallback((msg) => {
        if (typeof msg === 'string') return msg;
        if (typeof msg === 'object' && msg !== null) {
            return msg.message || msg.content || JSON.stringify(msg);
        }
        return '알림이 도착했습니다.';
    }, []);

    const handleClose = useCallback(() => {
        if (timerRef.current) {
            clearTimeout(timerRef.current);
        }
        onRemove(id);
    }, [id, onRemove]);

    const formattedTime = timestamp ? new Date(timestamp).toLocaleTimeString() : new Date().toLocaleTimeString();

    return (
        <div
            className={`alert-item priority-${getPriorityClass(type)}`}
            role="alert"
        >
            <div className="alert-icon">
                {getIcon(type)}
            </div>
            <div className="alert-content">
                <div className="alert-header">
                    <span className="alert-title">{publisher || '알림'}</span>
                    <span className="alert-time">{formattedTime}</span>
                </div>
                <div className="alert-message">
                    {renderMessage(content)}
                </div>
            </div>
            <button
                className="alert-close"
                onClick={handleClose}
                aria-label="Close notification"
            >
                <FaTimes />
            </button>
        </div>
    );
};

export default AlertSystem;