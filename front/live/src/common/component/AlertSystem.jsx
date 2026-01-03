import React, { useEffect } from 'react';
import { alertStateStore } from '../context/alertStateStore';
import './AlertSystem.css';
import { FaBell, FaTimes, FaExclamationCircle, FaInfoCircle, FaCheckCircle } from 'react-icons/fa';

const AlertSystem = () => {
    const { notifications, connect, disconnect, removeNotification } = alertStateStore();

    useEffect(() => {
        // 컴포넌트 마운트 시 웹소켓 연결
        connect();

        // 언마운트 시 연결 해제
        return () => {
            disconnect();
        };
    }, [connect, disconnect]);

    return (
        <div className="alert-container">
            {notifications.map((notification) => (
                <AlertItem
                    key={notification.id}
                    notification={notification}
                    onRemove={removeNotification}
                />
            ))}
        </div>
    );
};

const AlertItem = ({ notification, onRemove }) => {
    const { id, content, priority, timestamp, sender } = notification;

    useEffect(() => {
        // 5초 후 자동 삭제
        const timer = setTimeout(() => {
            onRemove(id);
        }, 5000);
        return () => clearTimeout(timer);
    }, [id, onRemove]);

    // 우선순위에 따른 아이콘 결정
    const getIcon = (p) => {
        switch (p) {
            case 'HIGH': return <FaExclamationCircle />;
            case 'NORMAL': return <FaInfoCircle />;
            case 'LOW':
            default: return <FaBell />;
        }
    };

    // 메시지 내용 처리 (객체이거나 문자열일 수 있음)
    const renderMessage = (msg) => {
        if (typeof msg === 'string') return msg;
        if (typeof msg === 'object' && msg !== null) {
            return msg.message || msg.content || JSON.stringify(msg);
        }
        return '알림이 도착했습니다.';
    };

    return (
        <div className={`alert-item priority-${priority || 'LOW'}`} role="alert">
            <div className="alert-icon">
                {getIcon(priority)}
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
                onClick={() => onRemove(id)}
                aria-label="Close notification"
            >
                <FaTimes />
            </button>
        </div>
    );
};

export default AlertSystem;
