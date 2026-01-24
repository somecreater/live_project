import React, { useState, useRef, useEffect, useCallback } from 'react';
import { Dropdown, Badge, Button } from 'react-bootstrap';
import { FaBell, FaTrash, FaCheckDouble, FaVideo, FaBroadcastTower, FaEdit, FaTrashAlt, FaComment, FaUser, FaCheckCircle } from 'react-icons/fa';
import { alertStateStore } from '../context/alertStateStore';
import { userStateStore } from '../context/userStateStore';
import './NotificationCenter.css';

const NotificationCenter = () => {
    const notifications = alertStateStore((state) => state.notifications);
    const removeNotification = alertStateStore((state) => state.removeNotification);
    const clearNotifications = alertStateStore((state) => state.clearNotifications);
    const markNotificationsAsRead = alertStateStore((state) => state.markNotificationsAsRead);

    const fetchNotifications = alertStateStore((state) => state.fetchNotifications);

    // 로그인 상태 확인
    const isAuthenticated = userStateStore((state) => state.isAuthenticated);

    useEffect(() => {
        // 로그인 시 바로 알림 정보를 가져오도록 수정
        if (isAuthenticated) {
            fetchNotifications(0, 10, true);
        }
    }, [isAuthenticated, fetchNotifications]);

    const displayNotifications = isAuthenticated ? notifications : [];
    const sortedNotifications = [...displayNotifications].reverse();
    const unreadCount = isAuthenticated ? displayNotifications.filter(n => !n.read).length : 0;

    // AlertType에 따른 아이콘 결정
    const getIcon = (type) => {
        switch (type) {
            case 'VIDEO_UPLOAD': return <FaVideo className="icon-video" />;
            case 'STREAMING_START':
            case 'STREAMING_STOP': return <FaBroadcastTower className="icon-stream" />;
            case 'POST_UPLOAD':
            case 'POST_UPDATE': return <FaEdit className="icon-post" />;
            case 'POST_DELETE':
            case 'CHANNEL_DELETE': return <FaTrashAlt className="icon-delete" />;
            case 'REPLY_UPLOAD': return <FaComment className="icon-comment" />;
            case 'USER_UPDATE': return <FaUser className="icon-user" />;
            case 'CHANNEL_UPDATE': return <FaCheckCircle className="icon-channel" />;
            default: return <FaBell className="icon-default" />;
        }
    };

    const renderMessage = (msg) => {
        if (typeof msg === 'string') return msg;
        if (typeof msg === 'object' && msg !== null) {
            return msg.message || msg.content || JSON.stringify(msg);
        }
        return '새로운 알림이 있습니다.';
    };

    if (!isAuthenticated) {
        return null;
    }

    return (
        <Dropdown
            align="end"
            className="notification-dropdown"
            onToggle={(isOpen) => {
                if (isOpen && unreadCount > 0) {
                    markNotificationsAsRead();
                }
            }}
        >
            <Dropdown.Toggle as="div" className="notification-toggle">
                <div className="bell-container">
                    <FaBell className="bell-icon" />
                    {unreadCount > 0 && (
                        <Badge bg="danger" pill className="notification-badge">
                            {unreadCount > 99 ? '99+' : unreadCount}
                        </Badge>
                    )}
                </div>
            </Dropdown.Toggle>

            <Dropdown.Menu className="notification-menu shadow-lg">
                <div className="notification-header">
                    <h5>알림 목록</h5>
                    <div className="header-actions">
                        <Button
                            variant="link"
                            size="sm"
                            onClick={clearNotifications}
                            title="모두 지우기"
                        >
                            <FaTrash />
                        </Button>
                    </div>
                </div>

                <div className="notification-list custom-scrollbar">
                    {sortedNotifications.length > 0 ? (
                        sortedNotifications.map((notification) => (
                            <div key={notification.id} className={`notification-item ${notification.read ? 'read' : 'unread'}`}>
                                <div className="notification-icon-wrapper">
                                    {getIcon(notification.type)}
                                </div>
                                <div className="notification-content-wrapper">
                                    <div className="notification-sender">{notification.publisher || '시스템'}</div>
                                    <div className="notification-text">{renderMessage(notification.content)}</div>
                                    <div className="notification-time">{new Date(notification.timestamp).toLocaleString()}</div>
                                </div>
                                <button
                                    className="delete-item-btn"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        removeNotification(notification.id);
                                    }}
                                >
                                    <FaTrashAlt />
                                </button>
                            </div>
                        ))
                    ) : (
                        <div className="empty-notifications">
                            <FaBell className="empty-icon" />
                            <p>알림이 없습니다.</p>
                        </div>
                    )}
                </div>
            </Dropdown.Menu>
        </Dropdown>
    );
};

export default NotificationCenter;
