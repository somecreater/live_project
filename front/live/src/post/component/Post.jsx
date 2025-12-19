import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { Dropdown } from 'react-bootstrap';
import {
    FaHeart,
    FaRegHeart,
    FaComment,
    FaShare,
    FaEllipsisH,
    FaEdit,
    FaTrash,
    FaArrowRight,
    FaLock
} from 'react-icons/fa';
import './Post.css';

/**
 * Post 컴포넌트 - 개별 게시글 카드
 * @param {Object} post - 게시글 데이터
 * @param {function} onDetail - 상세보기 클릭 핸들러
 * @param {function} onEdit - 수정 클릭 핸들러
 * @param {function} onDelete - 삭제 클릭 핸들러
 * @param {boolean} isOwner - 채널 소유주 여부 (수정/삭제 권한)
 */
function Post({ post, onDetail, onEdit, onDelete, isOwner = false }) {
    const [isLiked, setIsLiked] = useState(false);
    const [likeCount, setLikeCount] = useState(post?.like || 0);

    if (!post) return null;

    const { id, title, content, category, channel_name, createdAt, commentable, visibility } = post;

    // 날짜 포맷팅
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        const now = new Date();
        const diff = (now - date) / 1000; // 초 단위

        if (diff < 60) return '방금 전';
        if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
        if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
        if (diff < 604800) return `${Math.floor(diff / 86400)}일 전`;

        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    };

    // 좋아요 토글
    const handleLikeClick = (e) => {
        e.stopPropagation();
        setIsLiked(!isLiked);
        setLikeCount(prev => isLiked ? prev - 1 : prev + 1);
        // TODO: API 호출 추가
    };

    // 작성자 이니셜
    const getInitial = () => {
        if (channel_name) return channel_name.charAt(0).toUpperCase();
        return 'U';
    };

    return (
        <article className="post-card">
            {/* 헤더 - 작성자 정보 */}
            <header className="post-header">
                <div className="post-author-avatar">
                    {getInitial()}
                </div>
                <div className="post-author-info">
                    <p className="post-author-name">{channel_name || '익명'}</p>
                    <div className="post-meta">
                        {category && (
                            <>
                                <span className="post-category-badge">{category}</span>
                                <span className="post-meta-separator"></span>
                            </>
                        )}
                        <span>{formatDate(createdAt)}</span>
                        {visibility === false && (
                            <>
                                <span className="post-meta-separator"></span>
                                <span className="text-secondary d-flex align-items-center gap-1" style={{ fontSize: '13px' }}>
                                    <FaLock size={12} /> 비공개
                                </span>
                            </>
                        )}
                    </div>
                </div>

                {/* 더보기 메뉴 */}
                <Dropdown align="end">
                    <Dropdown.Toggle
                        as="button"
                        className="post-actions-btn"
                        id={`post-dropdown-${id}`}
                    >
                        <FaEllipsisH />
                    </Dropdown.Toggle>
                    <Dropdown.Menu>
                        <Dropdown.Item onClick={() => onDetail?.(post)}>
                            <FaArrowRight className="me-2" /> 상세보기
                        </Dropdown.Item>
                        {/* 채널 소유주만 수정/삭제 메뉴 표시 */}
                        {isOwner && (
                            <>
                                <Dropdown.Divider />
                                <Dropdown.Item onClick={() => onEdit?.(post)}>
                                    <FaEdit className="me-2" /> 수정
                                </Dropdown.Item>
                                <Dropdown.Item
                                    onClick={() => onDelete?.(post)}
                                    className="text-danger"
                                >
                                    <FaTrash className="me-2" /> 삭제
                                </Dropdown.Item>
                            </>
                        )}
                    </Dropdown.Menu>
                </Dropdown>
            </header>

            {/* 본문 */}
            <div className="post-body">
                <h3
                    className="post-title"
                    onClick={() => onDetail?.(post)}
                >
                    {title || '제목 없음'}
                </h3>
                <div className="post-content post-content-preview">
                    {content || '내용이 없습니다.'}
                </div>
                {content && content.length > 200 && (
                    <span
                        className="post-read-more"
                        onClick={() => onDetail?.(post)}
                    >
                        더 보기 <FaArrowRight />
                    </span>
                )}
            </div>

            {/* 푸터 - 상호작용 버튼 */}
            <footer className="post-footer">
                <button
                    className={`post-stat ${isLiked ? 'active' : ''}`}
                    onClick={handleLikeClick}
                    aria-label={isLiked ? '좋아요 취소' : '좋아요'}
                >
                    {isLiked ? <FaHeart /> : <FaRegHeart />}
                    <span>{likeCount}</span>
                </button>

                <button
                    className="post-stat"
                    onClick={() => onDetail?.(post)}
                    aria-label="댓글"
                    disabled={!commentable}
                    style={{ opacity: commentable ? 1 : 0.5, cursor: commentable ? 'pointer' : 'default' }}
                >
                    <FaComment />
                </button>

                <button
                    className="post-stat"
                    onClick={(e) => {
                        e.stopPropagation();
                        // TODO: 공유 기능
                    }}
                    aria-label="공유"
                >
                    <FaShare />
                </button>
            </footer>
        </article>
    );
}

Post.propTypes = {
    post: PropTypes.shape({
        id: PropTypes.number,
        title: PropTypes.string,
        content: PropTypes.string,
        category: PropTypes.string,
        channel_name: PropTypes.string,
        channel_id: PropTypes.number,
        createdAt: PropTypes.string,
        updatedAt: PropTypes.string,
        like: PropTypes.number,
        unlike: PropTypes.number,
        visibility: PropTypes.bool,
        commentable: PropTypes.bool
    }),
    onDetail: PropTypes.func,
    onEdit: PropTypes.func,
    onDelete: PropTypes.func,
    isOwner: PropTypes.bool
};

export default Post;

