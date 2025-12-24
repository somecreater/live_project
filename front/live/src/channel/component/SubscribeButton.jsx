import React, { useState } from 'react';
import { Button } from 'react-bootstrap';
import { FaBell, FaBellSlash, FaPlus, FaCheck } from 'react-icons/fa';
import PropTypes from 'prop-types';
import './Subscription.css';
import ApiService from '../../common/api/ApiService';

/**
 * SubscribeButton 컴포넌트 (UI/Mock 버전 - API 생략)
 * @param {string} channelName - 구독할 채널 이름
 * @param {string} userLoginId - 현재 로그인한 유저 아이디
 * @param {function} onStatusChange - 상태 변경 시 콜백
 */
const SubscribeButton = ({ subscriptionData, is_subscribed, channelName, userLoginId, onStatusChange }) => {
    // SubscriptionDto 관련 Mock 상태
    const [subId, setSubId] = useState(subscriptionData ? subscriptionData.id : null);
    const [notification, setNotification] = useState(subscriptionData ? subscriptionData.notification : false);
    const [createdAt, setCreatedAt] = useState(subscriptionData.createdAt || null);

    // UI 관련 상태
    const [isSubscribed, setIsSubscribed] = useState(is_subscribed);

    const handleSubscribe = () => {
        const nextState = !isSubscribed;
        setIsSubscribed(nextState);

        if (nextState) {
            // 구독 신청 Mock 데이터 설정
            setSubId(Math.floor(Math.random() * 1000)); // 가상 ID
            setNotification(true);
            setCreatedAt(new Date().toISOString());
        } else {
            // 구독 취소 시 데이터 초기화
            setSubId(null);
            setNotification(false);
            setCreatedAt(null);
        }

        if (onStatusChange) onStatusChange(nextState);
    };

    const toggleNotification = async (e) => {
        e.stopPropagation();
        const nextNotify = !notification;
        const result = window.confirm(`알림을 ${nextNotify ? '켤' : '끄실'} 건가요?`);
        if (result){
            try{
                const response = await ApiService.subscription.update({
                    id: subId,
                    userLoginId: userLoginId,
                    channelName: channelName,
                    createdAt: null,
                    notification: nextNotify
                });
                const data= response.data;
                if(data.result){
                    alert('알림 설정이 변경되었습니다.');
                }
            }
            catch(error){
                console.error('알림 설정 변경 실패:', error);
                alert('알림 설정 변경에 실패했습니다. 다시 시도해주세요.');
                setNotification(!nextNotify);
                return;
            }
        }
        setNotification(nextNotify);
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
                <span className="visually-hidden">Mock ID: {subId}</span>
            )}
        </div>
    );
};

SubscribeButton.propTypes = {
    channelName: PropTypes.string.isRequired,
    userLoginId: PropTypes.string,
    onStatusChange: PropTypes.func
};

export default SubscribeButton;
