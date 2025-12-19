import React, { useState, useEffect, useCallback } from 'react';
import PropTypes from 'prop-types';
import { Modal } from 'react-bootstrap';
import {
    FaArrowLeft,
    FaHeart,
    FaRegHeart,
    FaComment,
    FaShare,
    FaEdit,
    FaTrash,
    FaEllipsisH
} from 'react-icons/fa';
import { Dropdown } from 'react-bootstrap';
import CommentSection from './CommentSection';
import ApiService from '../../common/api/ApiService';
import './Post.css';

/**
 * PostDetail 컴포넌트 - 게시글 상세 모달
 * @param {boolean} show - 모달 표시 여부
 * @param {Object} post - 게시글 데이터
 * @param {function} onHide - 닫기 핸들러
 * @param {function} onEdit - 수정 핸들러
 * @param {function} onDelete - 삭제 핸들러
 * @param {boolean} isOwner - 채널 소유주 여부
 */
function PostDetail({
    show,
    post,
    onHide,
    onEdit,
    onDelete,
    isOwner = false
}) {
    const [fullPost, setFullPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [comments, setComments] = useState([]);
    const [commentsLoading, setCommentsLoading] = useState(false);
    const [commentCount, setCommentCount] = useState(0);
    const [isLiked, setIsLiked] = useState(false);
    const [likeCount, setLikeCount] = useState(0);

    // 게시글 상세 조회
    const fetchPostDetail = useCallback(async () => {
        if (!post?.id) return;

        setLoading(true);
        try {
            const response = await ApiService.post.read(post.id);
            if (response.data && response.data.result) {
                setFullPost(response.data.post);
                /*
                댓글, 좋아요는 추후 생각    
                setComments(response.data.comments || []);
                setCommentCount(response.data.comment_count || 0);
                setLikeCount(response.data.post?.likeCount || 0);
                */
            }
        } catch (err) {
            console.error('게시글 상세 로딩 실패:', err);
        } finally {
            setLoading(false);
        }
    }, [post?.id]);

    useEffect(() => {
        if (show && post?.id) {
            fetchPostDetail();
        }
    }, [show, post?.id, fetchPostDetail]);

    // 날짜 포맷팅
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    // 좋아요 토글
    const handleLikeClick = () => {
        setIsLiked(!isLiked);
        setLikeCount(prev => isLiked ? prev - 1 : prev + 1);
        // TODO: API 호출 추가
    };

    // 댓글 추가
    const handleCommentAdd = async (postId, content) => {
        // TODO: 댓글 추가 API 호출
        console.log('Add comment:', postId, content);
        // 임시로 로컬에 추가
        const newComment = {
            id: Date.now(),
            content,
            authorId: 'user',
            authorName: 'User',
            createdAt: new Date().toISOString(),
            replyCount: 0,
            replies: []
        };
        setComments(prev => [newComment, ...prev]);
        setCommentCount(prev => prev + 1);
    };

    // 답글 추가
    const handleReplyAdd = async (commentId, content) => {
        // TODO: 답글 추가 API 호출
        console.log('Add reply:', commentId, content);
    };

    // 댓글 답글 로드
    const handleLoadReplies = async (commentId) => {
        // TODO: 답글 목록 API 호출
        console.log('Load replies for:', commentId);
    };

    // 이니셜
    const getInitial = () => {
        const name = fullPost?.channel_name || post?.channel_name;
        return name ? name.charAt(0).toUpperCase() : 'U';
    };

    const displayPost = fullPost || post;

    return (
        <Modal
            show={show}
            onHide={onHide}
            size="lg"
            centered
            scrollable
            className="post-detail-modal"
        >
            <Modal.Header className="border-0 pb-0">
                <button
                    className="btn btn-link text-muted p-0 me-3"
                    onClick={onHide}
                >
                    <FaArrowLeft size={20} />
                </button>
                <Modal.Title className="flex-grow-1" style={{ fontSize: '1.1rem' }}>
                    게시글
                </Modal.Title>

                {/* 채널 소유주만 수정/삭제 메뉴 표시 */}
                {isOwner && (
                    <Dropdown align="end">
                        <Dropdown.Toggle as="button" className="post-actions-btn">
                            <FaEllipsisH />
                        </Dropdown.Toggle>
                        <Dropdown.Menu>
                            <Dropdown.Item onClick={() => onEdit?.(displayPost)}>
                                <FaEdit className="me-2" /> 수정
                            </Dropdown.Item>
                            <Dropdown.Item onClick={() => onDelete?.(displayPost)} className="text-danger">
                                <FaTrash className="me-2" /> 삭제
                            </Dropdown.Item>
                        </Dropdown.Menu>
                    </Dropdown>
                )}
            </Modal.Header>

            <Modal.Body className="p-0">
                {loading ? (
                    <div className="post-loading py-5">
                        <div className="post-spinner"></div>
                    </div>
                ) : (
                    <div className="post-detail-card">
                        {/* 게시글 헤더 */}
                        <div className="post-detail-header">
                            <h2 className="post-detail-title">
                                {displayPost?.title || '제목 없음'}
                            </h2>

                            <div className="post-detail-author">
                                <div className="post-author-avatar">
                                    {getInitial()}
                                </div>
                                <div className="post-author-info">
                                    <p className="post-author-name mb-0">
                                        {displayPost?.channel_name || '익명'}
                                    </p>
                                    <span className="text-muted" style={{ fontSize: '13px' }}>
                                        {formatDate(displayPost?.createdAt)}
                                    </span>
                                </div>
                            </div>
                        </div>

                        {/* 게시글 내용 */}
                        <div className="post-detail-content">
                            {displayPost?.content || '내용이 없습니다.'}
                        </div>



                        {/* 상호작용 버튼 */}
                        <div className="post-footer" style={{ borderTop: '1px solid rgba(0,0,0,0.06)' }}>
                            <button
                                className={`post-stat ${isLiked ? 'active' : ''}`}
                                onClick={handleLikeClick}
                            >
                                {isLiked ? <FaHeart /> : <FaRegHeart />}
                                <span>{likeCount}</span>
                            </button>

                            <button className="post-stat">
                                <FaComment />
                                <span>{commentCount}</span>
                            </button>

                            <button className="post-stat">
                                <FaShare />
                            </button>
                        </div>

                        {/* 댓글 섹션 */}
                        <CommentSection
                            postId={displayPost?.id}
                            comments={comments}
                            totalCount={commentCount}
                            loading={commentsLoading}
                            onCommentAdd={handleCommentAdd}
                            onReplyAdd={handleReplyAdd}
                            onLoadReplies={handleLoadReplies}
                            commentable={displayPost?.commentable}
                        />
                    </div>
                )}
            </Modal.Body>
        </Modal>
    );
}

PostDetail.propTypes = {
    show: PropTypes.bool,
    post: PropTypes.object,
    onHide: PropTypes.func.isRequired,
    onEdit: PropTypes.func,
    onDelete: PropTypes.func,
    isOwner: PropTypes.bool
};

export default PostDetail;

