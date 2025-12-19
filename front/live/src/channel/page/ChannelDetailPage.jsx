import React, { useEffect, useState } from 'react';
import { Container, Spinner, Alert, Nav, Tab } from 'react-bootstrap';
import { useParams, useSearchParams } from 'react-router-dom';
import { FaHome, FaNewspaper, FaVideo, FaInfoCircle, FaList } from 'react-icons/fa';
import Cover from '../component/Cover';
import ChannelHome from '../component/ChannelHome';
import PostList from '../../post/component/PostList';
import ApiService from '../../common/api/ApiService';
import './ChannelDetailPage.css';

function ChannelDetailPage() {
    const { id } = useParams(); // URL에서 채널 ID 추출
    const [searchParams, setSearchParams] = useSearchParams();
    const [channel, setChannel] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 현재 활성 탭 (URL 파라미터에서 가져오거나 기본값 'home')
    const activeTab = searchParams.get('tab') || 'home';

    // 탭 변경 핸들러
    const handleTabChange = (tabKey) => {
        setSearchParams({ tab: tabKey });
    };

    // 현재 사용자 정보 조회
    useEffect(() => {
        const fetchCurrentUser = async () => {
            try {
                const response = await ApiService.user.info();
                if (response.data && response.data.result) {
                    setCurrentUser(response.data.user);
                }
            } catch (err) {
                // 로그인하지 않은 사용자일 수 있음 (무시)
                console.log('사용자 정보 조회:', err.response?.status === 401 ? '로그인 필요' : err);
            }
        };

        fetchCurrentUser();
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
                    setChannel(response.data.channel);
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

    // 채널 소유주 여부 확인
    const isOwner = currentUser && channel &&
        currentUser.login_id === channel.user_login_id;

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
                <div className="channel-header-info mb-4">
                    <h2 className="channel-name">{channel?.name || '채널'}</h2>
                    {channel?.description && (
                        <p className="text-muted channel-description">{channel.description}</p>
                    )}
                    {channel?.user_login_id && (
                        <p className="text-secondary channel-owner">
                            @{channel.user_login_id}
                        </p>
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


