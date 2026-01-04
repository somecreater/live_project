import React, { useEffect, useCallback, useRef, useState } from 'react';
import { alertStateStore } from '../context/alertStateStore';
import { userStateStore } from '../context/userStateStore';
import './AlertSystem.css';
import {
    FaBell, FaTimes, FaExclamationCircle, FaInfoCircle,
    FaCheckCircle, FaVideo, FaBroadcastTower, FaEdit,
    FaTrash, FaComment, FaUser
} from 'react-icons/fa';

const AlertSystem = () => {
    const notifications = alertStateStore((state) => state.notifications);
    const isConnected = alertStateStore((state) => state.isConnected);
    const isConnecting = alertStateStore((state) => state.isConnecting);
    const connectionError = alertStateStore((state) => state.connectionError);
    const removeNotification = alertStateStore((state) => state.removeNotification);
    const connect = alertStateStore((state) => state.connect);
    const disconnect = alertStateStore((state) => state.disconnect);
    const loadNotifications = alertStateStore((state) => state.loadNotifications);
    const isAuthenticated = userStateStore((state) => state.isAuthenticated);

    // 연결 상태 추적
    const hasInitialized = useRef(false);
    const connectAttempted = useRef(false);
    const mountTime = useRef(Date.now());
    const [activeToasts, setActiveToasts] = useState([]);
    const processedIds = useRef(new Set());

    // 초기화: 마운트 시 저장된 알림 로드
    useEffect(() => {
        loadNotifications();
    }, [loadNotifications]);

    // 인증 상태에 따른 웹소켓 연결 관리
    useEffect(() => {
        if (!hasInitialized.current && isAuthenticated) {
            hasInitialized.current = true;
            connectAttempted.current = true;

            // 즉시 연결 시도
            connect();
        }

        if (!isAuthenticated && hasInitialized.current) {
            disconnect();
            hasInitialized.current = false;
            connectAttempted.current = false;
        }
    }, [isAuthenticated, connect, disconnect]);

    // 신규 알림 감지하여 토스트에 추가
    useEffect(() => {
        const newNotifications = notifications.filter(n => {
            // 마운트 시점 이후 & 아직 토스트로 처리되지 않은 알림만 필터링
            return new Date(n.timestamp).getTime() > mountTime.current && !processedIds.current.has(n.id);
        });

        if (newNotifications.length > 0) {
            newNotifications.forEach(n => processedIds.current.add(n.id));
            setActiveToasts(prev => [...prev, ...newNotifications]);
        }
    }, [notifications]);

    const removeToast = useCallback((id) => {
        setActiveToasts(prev => prev.filter(t => t.id !== id));
    }, []);

    // 연결 상태 모니터링 (필요시 활성화)
    /*
    useEffect(() => {
        console.log('📊 Connection Status:', {
            isAuthenticated,
            isConnected,
            isConnecting,
            connectionError
        });
    }, [isAuthenticated, isConnected, isConnecting, connectionError]);
    */

    // cleanup
    /* 
    useEffect(() => {
        return () => {
            if (hasInitialized.current) {
                disconnect();
            }
        };
    }, [disconnect]);
    */

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
    const { id, content, priority, timestamp, publisher, eventType, eventSubType } = notification;
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

    // 알림 타입/서브타입에 따른 아이콘 결정
    const getIcon = useCallback((type, subType, p) => {
        switch (subType) {
            case 'VIDEO_UPLOAD': return <FaVideo />;
            case 'STREAMING_START':
            case 'STREAMING_STOP': return <FaBroadcastTower />;
            case 'POST_UPLOAD':
            case 'POST_UPDATE': return <FaEdit />;
            case 'POST_DELETE':
            case 'CHANNEL_DELETE': return <FaTrash />;
            case 'REPLY_UPLOAD': return <FaComment />;
            case 'USER_UPDATE': return <FaUser />;
            case 'CHANNEL_UPDATE': return <FaCheckCircle />;
            default: break;
        }

        switch (p) {
            case 'HIGH': return <FaExclamationCircle />;
            case 'NORMAL': return <FaInfoCircle />;
            case 'LOW':
            default: return <FaBell />;
        }
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

    return (
        <div
            className={`alert-item priority-${(priority || 'LOW').toLowerCase()}`}
            role="alert"
        >
            <div className="alert-icon">
                {getIcon(eventType, eventSubType, priority)}
            </div>
            <div className="alert-content">
                <div className="alert-header">
                    <span className="alert-title">{publisher || '알림'}</span>
                    <span className="alert-time">{new Date(timestamp).toLocaleTimeString()}</span>
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