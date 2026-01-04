import React, { useEffect } from 'react';
import { Container } from 'react-bootstrap';
import SubscriptionList from '../../channel/component/SubscriptionList';
import { FaUserFriends } from 'react-icons/fa';
import { userStateStore } from '../../common/context/userStateStore';

const MySubscriptionPage = () => {
    const { getUserInfo } = userStateStore();

    useEffect(() => {
        getUserInfo();
    }, [getUserInfo]);

    return (
        <Container className="py-5">
            <div className="d-flex align-items-center mb-4 pb-2 border-bottom">
                <div className="bg-danger bg-opacity-10 p-3 rounded-circle me-3">
                    <FaUserFriends size={28} className="text-danger" />
                </div>
                <div>
                    <h2 className="fw-bold mb-0">구독 중인 채널</h2>
                    <p className="text-muted mb-0">관심 있는 채널의 새로운 소식을 확인해보세요.</p>
                </div>
            </div>

            <SubscriptionList type="mine" />
        </Container>
    );
};

export default MySubscriptionPage;
