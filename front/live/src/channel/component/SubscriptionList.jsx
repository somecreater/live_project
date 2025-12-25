import React, { useState, useEffect, useCallback } from 'react';
import { Row, Col, Card, Button, Spinner } from 'react-bootstrap';
import { FaBell, FaBellSlash, FaTrash, FaUser, FaSync } from 'react-icons/fa';
import PropTypes from 'prop-types';
import ChannelOwnerAvatar from './ChannelOwnerAvatar';
import './Subscription.css';
import ApiService from '../../common/api/ApiService';
import { userStateStore } from '../../common/context/userStateStore';

/**
 * SubscriptionList 컴포넌트
 * @param {string} type - 'mine' (내가 구독한 채널), 'to_me' (나를 구독한 유저)
 * @param {string} name - 명시적인 전송 용 이름(null이면 store/storage에서 가져옴)
 */
const SubscriptionList = ({ type = 'mine', name }) => {
    const { user, channel, getUserInfo, getUserChannel } = userStateStore();
    const [subscriptions, setSubscriptions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [request, setRequest] = useState({
        name: '',
        keyword: null,
        page: 0,
        size: 10
    });

    const loginId = user?.loginId || localStorage.getItem("loginId");
    const channelName = channel?.name || localStorage.getItem("channelName");

    const fetchSubscriptions = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const isMine = type === 'mine';
            const apiMethod = isMine
                ? ApiService.subscription.my_subscription
                : ApiService.subscription.my_channel;

            const nameValue = name || (isMine ? loginId : channelName);

            if (!nameValue) {
                setSubscriptions([]);
                setLoading(false);
                return;
            }

            const currentRequest = {
                name: nameValue,
                keyword: null,
                page: 0,
                size: 10
            };

            setRequest(currentRequest);
            const response = await apiMethod(currentRequest);

            if (response.data) {
                const data = response.data;
                // 'result' 필드가 있으면 확인, 없으면 데이터가 있는지만 확인
                // 'result' 필드가 없거나 true이면 성공으로 간주, false이면 실패
                const isSuccess = data.result === undefined || data.result !== false;

                if (isSuccess) {
                    // 가능한 모든 필드명을 확인하여 목록 추출
                    const list = (data.subscription && data.subscription.content)
                        || data.subscription_list
                        || data.list
                        || data.content
                        || data.items
                        || [];
                    setSubscriptions(Array.isArray(list) ? list : []);
                } else {
                    setSubscriptions([]);
                }
            } else {
                setSubscriptions([]);
            }

        } catch (err) {
            console.error('구독 목록 로딩 실패:', err);
            setError('목록을 불러오는 중 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    }, [type, loginId, channelName, name]);

    useEffect(() => {
        fetchSubscriptions();
    }, [fetchSubscriptions]);

    const handleAction = async (sub, action) => {
        if (action === 'delete') {
            if (window.confirm('구독을 취소하시겠습니까?')) {
                try {
                    const response = await ApiService.subscription.delete(sub);
                    if (response.data.result) {
                        alert('구독이 취소되었습니다.');
                        setSubscriptions(prev => prev.filter(s => s.id !== sub.id));
                    } else {
                        alert('구독 취소에 실패했습니다.');
                    }
                } catch (error) {
                    console.error('구독 취소 실패:', error);
                    alert('오류가 발생했습니다.');
                }
            }
        } else if (action === 'toggle_notify') {
            const nextNotify = !sub.notification;
            try {
                const response = await ApiService.subscription.update({
                    ...sub,
                    notification: nextNotify
                });
                if (response.data.result) {
                    setSubscriptions(prev => prev.map(s =>
                        s.id === sub.id ? { ...s, notification: nextNotify } : s
                    ));
                }
            } catch (error) {
                console.error('알림 설정 실패:', error);
                alert('알림 설정 변경에 실패했습니다.');
            }
        }
    };

    if (loading) {
        return (
            <div className="text-center py-5">
                <Spinner animation="border" variant="danger" />
                <p className="mt-2 text-muted">목록을 불러오는 중...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="text-center py-5 text-danger">
                <p>{error}</p>
                <Button variant="outline-danger" onClick={fetchSubscriptions}>
                    <FaSync className="me-2" /> 다시 시도
                </Button>
            </div>
        );
    }

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
                                                구독일: {sub.createdAt ? new Date(sub.createdAt).toLocaleDateString() : '-'}
                                            </span>
                                        </div>
                                    </div>

                                    {type === 'mine' && (
                                        <div className="action-buttons-group d-flex gap-1">
                                            <Button
                                                variant="light"
                                                className="btn-circle-small"
                                                onClick={() => handleAction(sub, 'toggle_notify')}
                                                title={sub.notification ? "알림 끄기" : "알림 켜기"}
                                            >
                                                {sub.notification ? <FaBell className="text-primary" /> : <FaBellSlash className="text-muted" />}
                                            </Button>
                                            <Button
                                                variant="light"
                                                className="btn-circle-small text-danger"
                                                onClick={() => handleAction(sub, 'delete')}
                                                title="구독 취소"
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
    type: PropTypes.oneOf(['mine', 'to_me']),
    name: PropTypes.string
};

export default SubscriptionList;
