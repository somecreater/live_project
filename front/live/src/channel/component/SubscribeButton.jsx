import React, { useState, useEffect } from 'react';
import { Button } from 'react-bootstrap';
import { FaBell, FaBellSlash, FaPlus, FaCheck } from 'react-icons/fa';
import PropTypes from 'prop-types';
import './Subscription.css';
import ApiService from '../../common/api/ApiService';

/**
 * SubscribeButton 컴포넌트
 * @param {object} subscriptionData - 구독 정보 객체
 * @param {boolean} is_subscribed - 구독 여부
 * @param {string} channelName - 구독할 채널 이름
 * @param {string} userLoginId - 현재 로그인한 유저 아이디
 * @param {function} onStatusChange - 상태 변경 시 콜백
 */
const SubscribeButton = ({ subscriptionData, is_subscribed, channelName, userLoginId, onStatusChange }) => {
    // SubscriptionDto 관련 상태
    const [subId, setSubId] = useState(subscriptionData ? subscriptionData.id : null);
    const [notification, setNotification] = useState(subscriptionData ? subscriptionData.notification : false);
    const [createdAt, setCreatedAt] = useState(subscriptionData ? subscriptionData.createdAt : null);

    // UI 관련 상태
    const [isSubscribed, setIsSubscribed] = useState(is_subscribed);

    // props 변경 시 내부 상태 동기화
    useEffect(() => {
        setIsSubscribed(is_subscribed);
        if (subscriptionData) {
            setSubId(subscriptionData.id);
            setNotification(subscriptionData.notification);
            setCreatedAt(subscriptionData.createdAt || null);
        } else {
            setSubId(null);
            setNotification(false);
            setCreatedAt(null);
        }
    }, [is_subscribed, subscriptionData]);

    const handleSubscribe = async () => {
        const nextState = !isSubscribed;

        if (nextState) {
            try {
                const response = await ApiService.subscription.insert({
                    id: null,
                    userLoginId: userLoginId,
                    channelName: channelName,
                    createdAt: null,
                    notification: true
                });
                const data = response.data;
                if (data.result) {
                    alert('구독이 완료되었습니다.');
                    const updatedSub = data.new_subscription;
                    setSubId(updatedSub.id);
                    setNotification(updatedSub.notification);
                    setCreatedAt(updatedSub.createdAt);
                    setIsSubscribed(true);
                    if (onStatusChange) onStatusChange(true, updatedSub);
                } else {
                    alert('구독 신청에 실패했습니다. 다시 시도해주세요.');
                }
            }
            catch (error) {
                console.error('구독 신청 실패:', error);
                alert('구독 신청에 실패했습니다. 다시 시도해주세요.');
            }
        } else {
            try {
                // 구독 취소 시 데이터 초기화
                const response = await ApiService.subscription.delete({
                    id: subId,
                    userLoginId: userLoginId,
                    channelName: channelName,
                    createdAt: createdAt,
                    notification: notification
                });
                if (response.data.result) {
                    alert('구독이 취소되었습니다.');
                    setSubId(null);
                    setNotification(false);
                    setCreatedAt(null);
                    setIsSubscribed(false);
                    if (onStatusChange) onStatusChange(false);
                } else {
                    alert('구독 취소에 실패했습니다. 다시 시도해주세요.');
                }
            } catch (error) {
                console.error('구독 취소 실패:', error);
                alert('구독 취소에 실패했습니다. 다시 시도해주세요.');
            }
        }
    };

    const toggleNotification = async (e) => {
        e.stopPropagation();
        const nextNotify = !notification;
        const result = window.confirm(`알림을 ${nextNotify ? '켬' : '끔'} 하시겠습니까?`);
        if (result) {
            try {
                const response = await ApiService.subscription.update({
                    id: subId,
                    userLoginId: userLoginId,
                    channelName: channelName,
                    createdAt: createdAt,
                    notification: nextNotify
                });
                const data = response.data;
                if (data.result) {
                    alert('알림 설정이 변경되었습니다.');
                    setNotification(nextNotify);
                }
            }
            catch (error) {
                console.error('알림 설정 변경 실패:', error);
                alert('알림 설정 변경에 실패했습니다. 다시 시도해주세요.');
            }
        }
    };

    return (
        <div className="subscribe-button-group d-flex align-items-center gap-2">
            <Button
                variant={isSubscribed ? "light" : "danger"}
                onClick={handleSubscribe}
                className={`premium-sub-btn rounded-pill px-4 ${isSubscribed ? 'subscribed' : ''}`}
            >
                <div className="d-flex align-items-center gap-2">
                    {isSubscribed ? (
                        <><FaCheck className="icon-anim" /> 구독중</>
                    ) : (
                        <><FaPlus className="icon-anim" /> 구독</>
                    )}
                </div>
            </Button>

            {isSubscribed && (
                <Button
                    variant="light"
                    onClick={toggleNotification}
                    className="notification-toggle-btn rounded-circle p-2"
                    title={notification ? "알림 끄기" : "알림 켜기"}
                >
                    {notification ?
                        <FaBell className="bell-active" /> :
                        <FaBellSlash className="text-muted" />
                    }
                </Button>
            )}

            {/* Mock 데이터 확인용 (개발 대기용) */}
            {isSubscribed && createdAt && (
                <span className="visually-hidden">Sub ID: {subId}</span>
            )}
        </div>
    );
};

SubscribeButton.propTypes = {
    subscriptionData: PropTypes.object,
    is_subscribed: PropTypes.bool,
    channelName: PropTypes.string.isRequired,
    userLoginId: PropTypes.string,
    onStatusChange: PropTypes.func
};

export default SubscribeButton;
