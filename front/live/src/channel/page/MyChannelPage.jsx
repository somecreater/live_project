import React, { useEffect, useState } from 'react';
import { Container, Spinner, Alert, Button, Modal, Nav, Tab } from 'react-bootstrap';
import { useSearchParams } from 'react-router-dom';
import { FaHome, FaNewspaper, FaVideo, FaInfoCircle, FaList, FaUsers } from 'react-icons/fa';
import Cover from '../component/Cover';
import ChannelHome from '../component/ChannelHome';
import PostList from '../../post/component/PostList';
import ApiService from '../../common/api/ApiService';
import SubscribeButton from '../component/SubscribeButton';
import SubscriptionList from '../component/SubscriptionList';
import './ChannelDetailPage.css';

function MyChannelPage() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [channel, setChannel] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showCoverModal, setShowCoverModal] = useState(false);
    const [coverKey, setCoverKey] = useState(0); // 커버 리프레시용

    // 현재 활성 탭 (URL 파라미터에서 가져오거나 기본값 'home')
    const activeTab = searchParams.get('tab') || 'home';

    // 탭 변경 핸들러
    const handleTabChange = (tabKey) => {
        setSearchParams({ tab: tabKey });
    };

    useEffect(() => {
        const fetchMyChannel = async () => {
            setLoading(true);
            setError(null);

            try {
                const response = await ApiService.channel.my_channel();
                if (response.data && response.data.result) {
                    setChannel(response.data.my_channel);
                } else {
                    setError('채널 정보를 찾을 수 없습니다.');
                }
            } catch (err) {
                console.error('채널 정보 로딩 실패:', err);
                setError('채널 정보를 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchMyChannel();
    }, []);

    const handleCoverEdit = () => {
        setShowCoverModal(true);
    };

    const handleCoverUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        try {
            await ApiService.cover.upload(formData);
            // 커버 컴포넌트 리프레시
            setCoverKey(prev => prev + 1);
            setShowCoverModal(false);
        } catch (err) {
            console.error('커버 업로드 실패:', err);
            alert('커버 이미지 업로드에 실패했습니다.');
        }
    };

    const handleCoverDelete = async () => {
        if (!window.confirm('정말 커버 이미지를 삭제하시겠습니까?')) return;

        try {
            await ApiService.cover.delete();
            // 커버 컴포넌트 리프레시
            setCoverKey(prev => prev + 1);
            setShowCoverModal(false);
        } catch (err) {
            console.error('커버 삭제 실패:', err);
            alert('커버 이미지 삭제에 실패했습니다.');
        }
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
        <div className="channel-detail-page my-channel-page">
            {/* 커버 이미지 */}
            <Cover
                key={coverKey}
                channelName={channel?.name}
                isEditable={true}
                onEditClick={handleCoverEdit}
            />

            {/* 채널 정보 영역 */}
            <Container className="mt-4">
                <div className="channel-header-info mb-4">
                    <h2 className="channel-name">{channel?.name || '내 채널'}</h2>
                    <div className="text-secondary mb-2">
                        구독자 {channel?.subscription_count || 0}명
                    </div>
                    {channel?.description && (
                        <p className="text-muted channel-description">{channel.description}</p>
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
                            <Nav.Link eventKey="subscribers" className="channel-tab-link">
                                <FaUsers className="me-2" />
                                구독자
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
                                isOwner={true}
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

                        {/* 구독자 탭 */}
                        <Tab.Pane eventKey="subscribers">
                            <div className="py-4">
                                <h4 className="mb-4">나를 구독 중인 사용자</h4>
                                <SubscriptionList type="to_me" name={channel?.name} />
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
                                            <span className="info-label">채널 이름</span>
                                            <span className="info-value">{channel?.name}</span>
                                        </div>
                                        <div className="about-info-item">
                                            <span className="info-label">구독자 수</span>
                                            <span className="info-value">{channel?.subscription_count || 0}명</span>
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

            {/* 커버 편집 모달 */}
            <Modal show={showCoverModal} onHide={() => setShowCoverModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>커버 이미지 편집</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div className="d-grid gap-3">
                        <div>
                            <label
                                htmlFor="cover-upload"
                                className="btn btn-primary w-100"
                                style={{ cursor: 'pointer' }}
                            >
                                새 이미지 업로드
                            </label>
                            <input
                                id="cover-upload"
                                type="file"
                                accept="image/*"
                                onChange={handleCoverUpload}
                                style={{ display: 'none' }}
                            />
                        </div>
                        <Button variant="outline-danger" onClick={handleCoverDelete}>
                            커버 이미지 삭제
                        </Button>
                    </div>
                    <p className="text-muted mt-3 mb-0" style={{ fontSize: '0.85rem' }}>
                        권장 크기: 2560 x 423 픽셀<br />
                        파일 형식: JPG, PNG (최대 6MB)
                    </p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowCoverModal(false)}>
                        닫기
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default MyChannelPage;

