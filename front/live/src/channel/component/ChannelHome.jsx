import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { Row, Col, Card, Badge } from 'react-bootstrap';
import {
    FaPlay,
    FaBroadcastTower,
    FaNewspaper,
    FaVideo,
    FaArrowRight,
    FaClock,
    FaEye
} from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import ApiService from '../../common/api/ApiService';
import './ChannelHome.css';

/**
 * ChannelHome 컴포넌트 - 채널 홈 탭
 * 대표 영상/스트리밍, 최신 동영상, 최신 게시글을 간략히 표시
 * @param {string} channelName - 채널 이름
 * @param {function} onTabChange - 탭 변경 핸들러
 */
function ChannelHome({ channelName, onTabChange }) {
    const navigate = useNavigate();
    const [featuredVideo, setFeaturedVideo] = useState(null);
    const [isLive, setIsLive] = useState(false);
    const [recentVideos, setRecentVideos] = useState([]);
    const [recentPosts, setRecentPosts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchHomeData = async () => {
            if (!channelName) return;

            setLoading(true);
            try {
                // 최신 게시글 조회 (3개)
                const postsResponse = await ApiService.post.list({
                    channel_name: channelName,
                    type: '',
                    keyword: '',
                    page: 0,
                    size: 3
                });

                if (postsResponse.data && postsResponse.data.result) {
                    setRecentPosts(postsResponse.data.post_page?.content || []);
                }

                // TODO: 대표 영상, 최신 동영상 API 연동
                // setFeaturedVideo(response.data.featured);
                // setRecentVideos(response.data.videos);
                // setIsLive(response.data.isLive);

            } catch (err) {
                console.error('채널 홈 데이터 로딩 실패:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchHomeData();
    }, [channelName]);

    // 날짜 포맷팅
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        const now = new Date();
        const diff = (now - date) / 1000;

        if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
        if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
        if (diff < 604800) return `${Math.floor(diff / 86400)}일 전`;

        return date.toLocaleDateString('ko-KR', {
            month: 'short',
            day: 'numeric'
        });
    };

    if (loading) {
        return (
            <div className="channel-home-loading">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="channel-home">
            {/* 대표 영상 / 라이브 스트리밍 섹션 */}
            <section className="home-section featured-section">
                <div className="featured-video-container">
                    {isLive ? (
                        // 라이브 스트리밍 중
                        <div className="featured-video live">
                            <div className="featured-placeholder">
                                <FaBroadcastTower size={64} />
                                <Badge bg="danger" className="live-badge">
                                    <span className="live-dot"></span>
                                    LIVE
                                </Badge>
                            </div>
                            <div className="featured-info">
                                <h3>라이브 스트리밍 중</h3>
                                <p className="text-muted">지금 바로 시청하세요!</p>
                                <button className="watch-btn">
                                    <FaPlay className="me-2" />
                                    시청하기
                                </button>
                            </div>
                        </div>
                    ) : featuredVideo ? (
                        // 대표 영상
                        <div className="featured-video">
                            <div className="featured-thumbnail">
                                {featuredVideo.thumbnailUrl ? (
                                    <img src={featuredVideo.thumbnailUrl} alt={featuredVideo.title} />
                                ) : (
                                    <div className="featured-placeholder">
                                        <FaVideo size={64} />
                                    </div>
                                )}
                                <div className="play-overlay">
                                    <FaPlay size={48} />
                                </div>
                            </div>
                            <div className="featured-info">
                                <h3>{featuredVideo.title}</h3>
                                <p className="text-muted">{featuredVideo.description}</p>
                            </div>
                        </div>
                    ) : (
                        // 대표 영상 없음
                        <div className="featured-empty">
                            <div className="featured-placeholder">
                                <FaVideo size={64} />
                            </div>
                            <div className="featured-info">
                                <h3>대표 영상이 없습니다</h3>
                                <p className="text-muted">아직 업로드된 영상이 없습니다.</p>
                            </div>
                        </div>
                    )}
                </div>
            </section>

            {/* 최신 동영상 섹션 */}
            <section className="home-section">
                <div className="section-header">
                    <h4>
                        <FaVideo className="me-2" />
                        최신 동영상
                    </h4>
                    <button
                        className="view-all-btn"
                        onClick={() => onTabChange?.('videos')}
                    >
                        모두 보기 <FaArrowRight />
                    </button>
                </div>

                {recentVideos.length > 0 ? (
                    <Row xs={1} md={2} lg={3} className="g-4">
                        {recentVideos.map((video) => (
                            <Col key={video.id}>
                                <Card className="video-card">
                                    <div className="video-thumbnail">
                                        {video.thumbnailUrl ? (
                                            <img src={video.thumbnailUrl} alt={video.title} />
                                        ) : (
                                            <div className="thumbnail-placeholder">
                                                <FaVideo />
                                            </div>
                                        )}
                                        <span className="video-duration">{video.duration || '00:00'}</span>
                                    </div>
                                    <Card.Body>
                                        <Card.Title className="video-title">{video.title}</Card.Title>
                                        <div className="video-meta">
                                            <span><FaEye /> {video.views || 0}</span>
                                            <span><FaClock /> {formatDate(video.createdAt)}</span>
                                        </div>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                ) : (
                    <div className="empty-section">
                        <FaVideo size={32} className="text-muted" />
                        <p>아직 업로드된 동영상이 없습니다.</p>
                    </div>
                )}
            </section>

            {/* 최신 게시글 섹션 */}
            <section className="home-section">
                <div className="section-header">
                    <h4>
                        <FaNewspaper className="me-2" />
                        최신 게시글
                    </h4>
                    <button
                        className="view-all-btn"
                        onClick={() => onTabChange?.('posts')}
                    >
                        모두 보기 <FaArrowRight />
                    </button>
                </div>

                {recentPosts.length > 0 ? (
                    <div className="posts-preview-list">
                        {recentPosts.map((post) => (
                            <div
                                key={post.id}
                                className="post-preview-item"
                                onClick={() => onTabChange?.('posts')}
                            >
                                <div className="post-preview-content">
                                    <h5 className="post-preview-title">{post.title || '제목 없음'}</h5>
                                    <p className="post-preview-text">
                                        {post.content?.substring(0, 100)}
                                        {post.content?.length > 100 && '...'}
                                    </p>
                                </div>
                                <div className="post-preview-meta">
                                    <span className="post-preview-author">
                                        @{post.channel_name || 'unknown'}
                                    </span>
                                    <span className="post-preview-date">
                                        {formatDate(post.createdAt)}
                                    </span>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="empty-section">
                        <FaNewspaper size={32} className="text-muted" />
                        <p>아직 작성된 게시글이 없습니다.</p>
                    </div>
                )}
            </section>
        </div>
    );
}

ChannelHome.propTypes = {
    channelName: PropTypes.string.isRequired,
    onTabChange: PropTypes.func
};

export default ChannelHome;
