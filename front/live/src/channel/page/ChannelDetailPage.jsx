import React, { useEffect, useState } from 'react';
import { Container, Spinner, Alert, Nav, Tab } from 'react-bootstrap';
import { useParams, useSearchParams } from 'react-router-dom';
import { FaHome, FaNewspaper, FaVideo, FaInfoCircle, FaList } from 'react-icons/fa';
import Cover from '../component/Cover';
import ChannelHome from '../component/ChannelHome';
import PostList from '../../post/component/PostList';
import ApiService from '../../common/api/ApiService';
import SubscribeButton from '../component/SubscribeButton';
import './ChannelDetailPage.css';

function ChannelDetailPage() {
    const { id } = useParams(); // URL에서 채널 ID 추출
    const loginId= localStorage.getItem("loginId");
    const [searchParams, setSearchParams] = useSearchParams();
    const [isSubscribed, setIsSubscribed] = useState(false);
    const [subscriptionData, setSubscriptionData] = useState(null);
    const [channel, setChannel] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 구속자 수 실시간 업데이트 시각화를 위한 상태 (선택적)
    const [subCount, setSubCount] = useState(0);

    // 현재 활성 탭 (URL 파라미터에서 가져오거나 기본값 'home')
    const activeTab = searchParams.get('tab') || 'home';

    // 탭 변경 핸들러
    const handleTabChange = (tabKey) => {
        setSearchParams({ tab: tabKey });
    };

    // 현재 사용자 정보 조회(일단 안씀)
    useEffect(() => {

    }, []);

    // 채널 정보 조회
    useEffect(() => {
        const fetchChannel = async () => {
            if (!id) {
                setError('채널 ID가 필요합니다.');
                setLoading(false);
                return;
            }

            setLoading(true);
            setError(null);

            try {
                const response = await ApiService.channel.info(id);
                if (response.data && response.data.result) {
                    const channelData = response.data.channel;
                    setChannel(channelData);
                    setSubCount(channelData.subscription_count || 0);
                } else {
                    setError('채널 정보를 찾을 수 없습니다.');
                }
            } catch (err) {
                console.error('채널 정보 로딩 실패:', err);
                if (err.response?.status === 404) {
                    setError('존재하지 않는 채널입니다.');
                } else {
                    setError('채널 정보를 불러오는데 실패했습니다.');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchChannel();
    }, [id]);

    // 구독 상태 조회
    useEffect(() => {
        const fetchSubscriptionStatus = async () => {
            if (!channel || !loginId) return;
            try {
                const response = await ApiService.subscription.is_subscribed(channel.name);
                if (response.data && response.data.is_subscription !== false) {
                    setIsSubscribed(response.data.is_subscription);
                    setSubscriptionData(response.data.subscription);
                } else {
                    setIsSubscribed(false);
                    setSubscriptionData(null);
                }
            } catch (err) {
                console.error('구독 상태 조회 실패:', err);
            }
        };

        fetchSubscriptionStatus();
    }, [channel, loginId]);

    // 채널 소유주 여부 확인
    const isOwner = loginId && channel &&
        loginId === channel.user_login_id;

    // 구독 상태 변경 시 처리 (필요시 count 증감)
    const handleSubStatusChange = (isSubscribing) => {
        setSubCount(prev => isSubscribing ? prev + 1 : prev - 1);
    };

    if (loading) {
        return (
            <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '50vh' }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="mt-4">
                <Alert variant="danger">{error}</Alert>
            </Container>
        );
    }

    return (
        <div className="channel-detail-page">
            {/* 커버 이미지 */}
            <Cover
                channelName={channel?.name}
                isEditable={isOwner}
            />

            {/* 채널 정보 영역 */}
            <Container className="mt-4">
                <div className="channel-header-info mb-4 d-flex justify-content-between align-items-end">
                    <div>
                        <h2 className="channel-name mb-1">{channel?.name || '채널'}</h2>
                        <div className="text-secondary mb-2 d-flex align-items-center gap-2">
                            <span>@{channel?.user_login_id}</span>
                            <span className="text-muted">•</span>
                            <span>구독자 {subCount.toLocaleString()}명</span>
                        </div>
                        {channel?.description && (
                            <p className="text-muted channel-description mb-0">{channel.description}</p>
                        )}
                    </div>

                    {/* 구독 버튼 (자기 채널이 아닐 때만 표시) */}
                    {!isOwner && (
                        <div className="pb-2">
                            <SubscribeButton
                                subscriptionData={subscriptionData}
                                channelName={channel.name}
                                userLoginId={loginId}
                                is_subscribed={isSubscribed}
                                onStatusChange={handleSubStatusChange}
                            />
                        </div>
                    )}
                </div>

                {/* 탭 네비게이션 */}
                <Tab.Container activeKey={activeTab} onSelect={handleTabChange}>
                    <Nav variant="tabs" className="channel-tabs mb-4">
                        <Nav.Item>
                            <Nav.Link eventKey="home" className="channel-tab-link">
                                <FaHome className="me-2" />
                                홈
                            </Nav.Link>
                        </Nav.Item>
                        <Nav.Item>
                            <Nav.Link eventKey="posts" className="channel-tab-link">
                                <FaNewspaper className="me-2" />
                                게시글
                            </Nav.Link>
                        </Nav.Item>
                        <Nav.Item>
                            <Nav.Link eventKey="videos" className="channel-tab-link">
                                <FaVideo className="me-2" />
                                동영상
                            </Nav.Link>
                        </Nav.Item>
                        <Nav.Item>
                            <Nav.Link eventKey="playlists" className="channel-tab-link">
                                <FaList className="me-2" />
                                재생목록
                            </Nav.Link>
                        </Nav.Item>
                        <Nav.Item>
                            <Nav.Link eventKey="about" className="channel-tab-link">
                                <FaInfoCircle className="me-2" />
                                정보
                            </Nav.Link>
                        </Nav.Item>
                    </Nav>

                    <Tab.Content>
                        {/* 홈 탭 */}
                        <Tab.Pane eventKey="home">
                            <ChannelHome
                                channelName={channel?.name}
                                onTabChange={handleTabChange}
                            />
                        </Tab.Pane>

                        {/* 게시글 탭 */}
                        <Tab.Pane eventKey="posts">
                            <PostList
                                channelName={channel?.name}
                                isOwner={isOwner}
                            />
                        </Tab.Pane>

                        {/* 동영상 탭 */}
                        <Tab.Pane eventKey="videos">
                            <div className="tab-placeholder">
                                <FaVideo size={48} className="text-muted mb-3" />
                                <h4>동영상</h4>
                                <p className="text-muted">아직 업로드된 동영상이 없습니다.</p>
                            </div>
                        </Tab.Pane>

                        {/* 재생목록 탭 */}
                        <Tab.Pane eventKey="playlists">
                            <div className="tab-placeholder">
                                <FaList size={48} className="text-muted mb-3" />
                                <h4>재생목록</h4>
                                <p className="text-muted">아직 생성된 재생목록이 없습니다.</p>
                            </div>
                        </Tab.Pane>

                        {/* 정보 탭 */}
                        <Tab.Pane eventKey="about">
                            <div className="channel-about-section">
                                <div className="about-card">
                                    <h5>채널 설명</h5>
                                    <p>{channel?.description || '채널 설명이 없습니다.'}</p>
                                </div>
                                <div className="about-card">
                                    <h5>채널 정보</h5>
                                    <div className="about-info-list">
                                        <div className="about-info-item">
                                            <span className="info-label">채널 소유자</span>
                                            <span className="info-value">@{channel?.user_login_id}</span>
                                        </div>
                                        {channel?.createdAt && (
                                            <div className="about-info-item">
                                                <span className="info-label">개설일</span>
                                                <span className="info-value">
                                                    {new Date(channel.createdAt).toLocaleDateString('ko-KR', {
                                                        year: 'numeric',
                                                        month: 'long',
                                                        day: 'numeric'
                                                    })}
                                                </span>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </Tab.Pane>
                    </Tab.Content>
                </Tab.Container>
            </Container>
        </div>
    );
}

export default ChannelDetailPage;


