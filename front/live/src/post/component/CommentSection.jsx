import React, { useState, useEffect, useCallback } from 'react';
import PropTypes from 'prop-types';
import { FaPaperPlane, FaReply, FaChevronDown, FaChevronUp, FaEllipsisV, FaEdit, FaTrash } from 'react-icons/fa';
import { Dropdown } from 'react-bootstrap';
import './Post.css';

/**
 * Comment 컴포넌트 - 개별 댓글/답글 아이템
 */
function CommentItem({
    comment,
    isReply = false,
    currentUserId,
    onReply,
    onEdit,
    onDelete,
    onLoadReplies,
    repliesVisible = false,
    repliesLoading = false,
    commentable = true
}) {
    const { id, content, authorName, authorId, createdAt, replyCount = 0, replies = [] } = comment;
    const [showReplyInput, setShowReplyInput] = useState(false);
    const [showReplies, setShowReplies] = useState(repliesVisible);
    const [replyContent, setReplyContent] = useState('');

    const isOwner = currentUserId && authorId === currentUserId;

    // 날짜 포맷팅
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        const now = new Date();
        const diff = (now - date) / 1000;

        if (diff < 60) return '방금 전';
        if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
        if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;

        return date.toLocaleDateString('ko-KR', {
            month: 'short',
            day: 'numeric'
        });
    };

    // 답글 토글
    const handleToggleReplies = () => {
        if (!showReplies && onLoadReplies) {
            onLoadReplies(id);
        }
        setShowReplies(!showReplies);
    };

    // 답글 제출
    const handleReplySubmit = () => {
        if (!replyContent.trim()) return;
        onReply?.(id, replyContent.trim());
        setReplyContent('');
        setShowReplyInput(false);
    };

    // 이니셜
    const getInitial = () => {
        if (authorName) return authorName.charAt(0).toUpperCase();
        if (authorId) return authorId.charAt(0).toUpperCase();
        return 'U';
    };

    return (
        <div className={isReply ? 'reply-item' : 'comment-item'}>
            <div className="comment-header">
                <div className="comment-avatar" style={{ width: isReply ? 28 : 36, height: isReply ? 28 : 36, fontSize: isReply ? 12 : 14 }}>
                    {getInitial()}
                </div>
                <span className="comment-author">{authorName || authorId || '익명'}</span>
                <span className="comment-date">{formatDate(createdAt)}</span>

                {/* 더보기 메뉴 */}
                <Dropdown align="end" className="ms-auto">
                    <Dropdown.Toggle as="button" className="post-actions-btn" style={{ width: 28, height: 28 }}>
                        <FaEllipsisV size={12} />
                    </Dropdown.Toggle>
                    <Dropdown.Menu>
                        {!isReply && commentable && (
                            <Dropdown.Item onClick={() => setShowReplyInput(true)}>
                                <FaReply className="me-2" /> 답글 달기
                            </Dropdown.Item>
                        )}
                        {isOwner && (
                            <>
                                <Dropdown.Item onClick={() => onEdit?.(comment)}>
                                    <FaEdit className="me-2" /> 수정
                                </Dropdown.Item>
                                <Dropdown.Item onClick={() => onDelete?.(comment)} className="text-danger">
                                    <FaTrash className="me-2" /> 삭제
                                </Dropdown.Item>
                            </>
                        )}
                    </Dropdown.Menu>
                </Dropdown>
            </div>

            <p className="comment-content" style={{ paddingLeft: isReply ? 38 : 46 }}>
                {content}
            </p>

            {/* 답글 토글 버튼 */}
            {!isReply && replyCount > 0 && (
                <button className="reply-toggle-btn" onClick={handleToggleReplies}>
                    {showReplies ? <FaChevronUp /> : <FaChevronDown />}
                    {' '}답글 {replyCount}개 {showReplies ? '숨기기' : '보기'}
                </button>
            )}

            {/* 답글 입력 */}
            {showReplyInput && (
                <div className="comment-input-container" style={{ marginLeft: 46, marginTop: 12 }}>
                    <textarea
                        className="comment-input"
                        placeholder="답글을 입력하세요..."
                        value={replyContent}
                        onChange={(e) => setReplyContent(e.target.value)}
                        rows={1}
                    />
                    <button
                        className="comment-submit-btn"
                        onClick={handleReplySubmit}
                        disabled={!replyContent.trim()}
                    >
                        <FaPaperPlane size={16} />
                    </button>
                </div>
            )}

            {/* 답글 목록 */}
            {!isReply && showReplies && (
                <div className="comment-replies">
                    {repliesLoading ? (
                        <div className="text-center py-2">
                            <div className="spinner-border spinner-border-sm text-primary" />
                        </div>
                    ) : (
                        replies.map((reply) => (
                            <CommentItem
                                key={reply.id}
                                comment={reply}
                                isReply={true}
                                onEdit={onEdit}
                                onDelete={onDelete}
                                commentable={commentable}
                            />
                        ))
                    )}
                </div>
            )}
        </div>
    );
}

CommentItem.propTypes = {
    comment: PropTypes.shape({
        id: PropTypes.number,
        content: PropTypes.string,
        authorName: PropTypes.string,
        authorId: PropTypes.string,
        createdAt: PropTypes.string,
        replyCount: PropTypes.number,
        replies: PropTypes.array
    }).isRequired,
    isReply: PropTypes.bool,
    currentUserId: PropTypes.string,
    onReply: PropTypes.func,
    onEdit: PropTypes.func,
    onDelete: PropTypes.func,
    onLoadReplies: PropTypes.func,
    repliesVisible: PropTypes.bool,
    repliesLoading: PropTypes.bool,
    commentable: PropTypes.bool
};

/**
 * CommentSection 컴포넌트 - 댓글 섹션 전체
 * @param {number} postId - 게시글 ID
 * @param {string} currentUserId - 현재 로그인한 사용자 ID
 * @param {function} onCommentAdd - 댓글 추가 핸들러
 * @param {function} onCommentEdit - 댓글 수정 핸들러
 * @param {function} onCommentDelete - 댓글 삭제 핸들러
 */
function CommentSection({
    postId,
    comments = [],
    totalCount = 0,
    currentUserId,
    loading = false,
    onCommentAdd,
    onCommentEdit,
    onCommentDelete,
    onReplyAdd,
    onLoadReplies,
    commentable = true
}) {
    const [newComment, setNewComment] = useState('');
    const [submitting, setSubmitting] = useState(false);

    // 새 댓글 작성
    const handleCommentSubmit = async () => {
        if (!newComment.trim() || submitting) return;

        setSubmitting(true);
        try {
            await onCommentAdd?.(postId, newComment.trim());
            setNewComment('');
        } catch (err) {
            console.error('댓글 작성 실패:', err);
        } finally {
            setSubmitting(false);
        }
    };

    // 답글 작성
    const handleReplySubmit = async (commentId, content) => {
        try {
            await onReplyAdd?.(commentId, content);
        } catch (err) {
            console.error('답글 작성 실패:', err);
        }
    };

    // Enter 키 처리 (Shift+Enter는 줄바꿈)
    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleCommentSubmit();
        }
    };

    return (
        <section className="comments-section">
            <div className="comments-header">
                <h4 className="comments-title">댓글</h4>
                <span className="comments-count">{totalCount}</span>
            </div>

            {/* 댓글 입력 */}
            {commentable ? (
                <div className="comment-input-container">
                    <textarea
                        className="comment-input"
                        placeholder="댓글을 입력하세요..."
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                        onKeyDown={handleKeyDown}
                        rows={1}
                        disabled={submitting}
                    />
                    <button
                        className="comment-submit-btn"
                        onClick={handleCommentSubmit}
                        disabled={!newComment.trim() || submitting}
                    >
                        {submitting ? (
                            <div className="spinner-border spinner-border-sm" />
                        ) : (
                            <FaPaperPlane size={16} />
                        )}
                    </button>
                </div>
            ) : (
                <div className="text-center py-3 text-muted border rounded bg-light">
                    댓글 사용이 중지된 게시글입니다.
                </div>
            )}

            {/* 댓글 목록 */}
            {loading ? (
                <div className="post-loading py-4">
                    <div className="post-spinner" style={{ width: 30, height: 30 }}></div>
                </div>
            ) : comments.length > 0 ? (
                <div className="mt-4">
                    {comments.map((comment) => (
                        <CommentItem
                            key={comment.id}
                            comment={comment}
                            currentUserId={currentUserId}
                            onReply={handleReplySubmit}
                            onEdit={onCommentEdit}
                            onDelete={onCommentDelete}
                            onLoadReplies={onLoadReplies}
                            commentable={commentable}
                        />
                    ))}
                </div>
            ) : (
                <p className="text-muted text-center py-4">
                    아직 댓글이 없습니다. 첫 댓글을 남겨보세요!
                </p>
            )}
        </section>
    );
}

CommentSection.propTypes = {
    postId: PropTypes.number.isRequired,
    comments: PropTypes.array,
    totalCount: PropTypes.number,
    currentUserId: PropTypes.string,
    loading: PropTypes.bool,
    onCommentAdd: PropTypes.func,
    onCommentEdit: PropTypes.func,
    onCommentDelete: PropTypes.func,
    onReplyAdd: PropTypes.func,
    onLoadReplies: PropTypes.func,
    commentable: PropTypes.bool
};

export { CommentSection, CommentItem };
export default CommentSection;
