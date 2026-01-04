import React, { useEffect, useCallback, useRef } from 'react';
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
    const isAuthenticated = userStateStore((state) => state.isAuthenticated);

    // 연결 상태 추적
    const hasInitialized = useRef(false);
    const connectAttempted = useRef(false);

    useEffect(() => {
        console.log('🔄 AlertSystem Effect - Auth:', isAuthenticated, 'Initialized:', hasInitialized.current);

        if (!hasInitialized.current && isAuthenticated) {
            console.log('🔌 First time connection attempt...');
            hasInitialized.current = true;
            connectAttempted.current = true;

            // 약간의 지연을 두고 연결 (토큰이 완전히 저장될 시간 확보)
            const timer = setTimeout(() => {
                connect();
            }, 100);

            return () => clearTimeout(timer);
        }

        if (!isAuthenticated && hasInitialized.current) {
            console.log('🔌 User logged out - disconnecting...');
            disconnect();
            hasInitialized.current = false;
            connectAttempted.current = false;
        }
    }, [isAuthenticated, connect, disconnect]);

    // 연결 상태 모니터링
    useEffect(() => {
        console.log('📊 Connection Status:', {
            isAuthenticated,
            isConnected,
            isConnecting,
            connectionError
        });
    }, [isAuthenticated, isConnected, isConnecting, connectionError]);

    // cleanup
    useEffect(() => {
        return () => {
            console.log('🧹 AlertSystem unmounting - cleaning up...');
            if (hasInitialized.current) {
                disconnect();
            }
        };
    }, [disconnect]);

    return (
        <>
            {/* 연결 상태 표시 (개발용 - 프로덕션에서는 제거) */}
            {process.env.NODE_ENV === 'development' && (
                <div style={{
                    position: 'fixed',
                    top: '10px',
                    right: '10px',
                    padding: '10px',
                    background: isConnected ? '#4CAF50' : isConnecting ? '#FFC107' : '#F44336',
                    color: 'white',
                    borderRadius: '5px',
                    fontSize: '12px',
                    zIndex: 10000
                }}>
                    WS: {isConnected ? '연결됨' : isConnecting ? '연결중...' : '연결안됨'}
                    {connectionError && <div style={{ fontSize: '10px' }}>Error: {connectionError}</div>}
                </div>
            )}

            <div className="alert-container">
                {notifications.map((notification) => (
                    <AlertItem
                        key={notification.id}
                        notification={notification}
                        onRemove={removeNotification}
                    />
                ))}
            </div>
        </>
    );
};

const AlertItem = ({ notification, onRemove }) => {
    const { id, content, priority, timestamp, sender, eventType, eventSubType } = notification;
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
                    <span className="alert-title">{sender || '알림'}</span>
                    <span className="alert-time">{timestamp}</span>
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