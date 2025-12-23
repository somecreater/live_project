import React, { useState } from 'react';
import { Row, Col, Card, Button } from 'react-bootstrap';
import { FaBell, FaBellSlash, FaTrash, FaUser } from 'react-icons/fa';
import PropTypes from 'prop-types';
import ChannelOwnerAvatar from './ChannelOwnerAvatar';
import './Subscription.css';

/**
 * SubscriptionList 컴포넌트 (스타일링/이벤트 집중 버전 - API 생략)
 * @param {string} type - 'mine' (내가 구독한 채널), 'to_me' (나를 구독한 유저)
 */
const SubscriptionList = ({ type = 'mine' }) => {
    // 스타일 및 인터랙션 확인을 위한 Mock 데이터
    const mockData = type === 'mine' ? [
        { id: 1, channelName: '테크 브리핑', notification: true, createdAt: '2023-10-01' },
        { id: 2, channelName: '공부하는 개발자', notification: false, createdAt: '2023-11-15' },
        { id: 3, channelName: '여행의 정석', notification: true, createdAt: '2023-12-20' },
    ] : [
        { id: 10, userLoginId: 'user_alpha', createdAt: '2024-01-05' },
        { id: 11, userLoginId: 'tester_01', createdAt: '2024-02-10' },
        { id: 12, userLoginId: 'creative_mind', createdAt: '2024-03-01' },
    ];

    const [subscriptions, setSubscriptions] = useState(mockData);

    const handleAction = (sub, action) => {
        if (action === 'delete') {
            if (window.confirm('구독을 취소하시겠습니까?')) {
                setSubscriptions(prev => prev.filter(s => s.id !== sub.id));
            }
        } else if (action === 'toggle_notify') {
            // 알림 여부 변경 (추후 update API 호출부)
            setSubscriptions(prev => prev.map(s =>
                s.id === sub.id ? { ...s, notification: !s.notification } : s
            ));
        }
    };

    if (subscriptions.length === 0) {
        return (
            <div className="text-center py-5 text-muted bg-light rounded-4 border">
                <FaUser size={48} className="mb-3 opacity-25" />
                <h5>{type === 'mine' ? '구독 중인 채널이 없습니다.' : '구독자가 없습니다.'}</h5>
                <p>새로운 채널을 탐색하고 소식을 받아보세요!</p>
            </div>
        );
    }

    return (
        <div className="subscription-list-container">
            <Row xs={1} md={2} lg={3} className="g-4">
                {subscriptions.map((sub) => {
                    const displayName = type === 'mine' ? sub.channelName : sub.userLoginId;
                    const avatarUserId = type === 'mine' ? null : sub.userLoginId;

                    return (
                        <Col key={sub.id}>
                            <Card className="subscription-card-premium border-0 h-100 shadow-sm">
                                <Card.Body className="d-flex align-items-center p-3">
                                    <div className="avatar-wrapper me-3">
                                        <ChannelOwnerAvatar userId={avatarUserId} size={50} />
                                    </div>
                                    <div className="flex-grow-1 overflow-hidden">
                                        <h6 className="channel-title mb-1 text-truncate">{displayName}</h6>
                                        <div className="sub-info d-flex align-items-center gap-2">
                                            <span className="date-badge">
                                                구독일: {new Date(sub.createdAt).toLocaleDateString()}
                                            </span>
                                        </div>
                                    </div>

                                    {type === 'mine' && (
                                        <div className="action-buttons-group d-flex gap-1">
                                            <Button
                                                variant="light"
                                                className="btn-circle-small"
                                                onClick={() => handleAction(sub, 'toggle_notify')}
                                            >
                                                {sub.notification ? <FaBell className="text-primary" /> : <FaBellSlash className="text-muted" />}
                                            </Button>
                                            <Button
                                                variant="light"
                                                className="btn-circle-small text-danger"
                                                onClick={() => handleAction(sub, 'delete')}
                                            >
                                                <FaTrash />
                                            </Button>
                                        </div>
                                    )}
                                </Card.Body>
                            </Card>
                        </Col>
                    );
                })}
            </Row>
        </div>
    );
};

SubscriptionList.propTypes = {
    type: PropTypes.oneOf(['mine', 'to_me'])
};

export default SubscriptionList;
