import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { FaTimes, FaPaperPlane } from 'react-icons/fa';
import '../component/Post.css';

// 카테고리 옵션 정의
const CATEGORY_OPTIONS = [
    { value: '', label: '카테고리를 선택해주세요' },
    { value: '일반', label: '일반' },
    { value: '공지', label: '공지' },
    { value: '이벤트', label: '이벤트' },
    { value: '질문', label: '질문' },
    { value: '정보', label: '정보' },
    { value: '리뷰', label: '리뷰' },
    { value: '기타', label: '기타' }
];

/**
 * PostForm 컴포넌트 - 게시글 작성/수정 폼
 * @param {Object} post - 수정할 게시글 데이터 (null이면 신규 작성)
 * @param {function} onSubmit - 제출 핸들러
 * @param {function} onCancel - 취소 핸들러
 */
function PostForm({ post, onSubmit, onCancel }) {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [category, setCategory] = useState('');
    const [visibility, setVisibility] = useState(true);
    const [commentable, setCommentable] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [errors, setErrors] = useState({});

    const isEditing = !!post;

    // 수정 모드일 경우 데이터 초기화
    useEffect(() => {
        if (post) {
            console.log('PostForm editing post:', post);
            setTitle(post.title || '');
            setContent(post.content || '');
            setCategory(post.category || '');

            // 0이나 false도 false로 처리, null/undefined만 true
            const initBool = (val) => (val === undefined || val === null) ? true : !!val;

            setVisibility(initBool(post.visibility));
            setCommentable(initBool(post.commentable));
        } else {
            setTitle('');
            setContent('');
            setCategory('');
            setVisibility(true);
            setCommentable(true);
        }
        setErrors({});
    }, [post]);

    // 유효성 검사 (백엔드 조건에 맞게)
    const validate = () => {
        const newErrors = {};

        if (!title.trim()) {
            newErrors.title = '제목을 입력해주세요.';
        } else if (title.length > 100) {
            newErrors.title = '제목은 100자 이내로 입력해주세요.';
        }

        if (!content.trim()) {
            newErrors.content = '내용을 입력해주세요.';
        } else if (content.length > 5000) {
            newErrors.content = '내용은 5000자 이내로 입력해주세요.';
        }

        if (!category.trim()) {
            newErrors.category = '카테고리를 선택해주세요.';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    // 제출
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validate()) return;

        setSubmitting(true);

        try {
            await onSubmit({
                title: title.trim(),
                content: content.trim(),
                category: category.trim(),
                visibility,
                commentable
            });
        } catch (err) {
            console.error('게시글 저장 실패:', err);
            setErrors({ submit: '저장에 실패했습니다. 다시 시도해주세요.' });
        } finally {
            setSubmitting(false);
        }
    };

    // 폼이 유효한지 확인
    const isFormValid = title.trim() && content.trim() && category.trim();

    return (
        <div className="post-form-container">
            <div className="post-form-header">
                <h3 className="post-form-title">
                    {isEditing ? '게시글 수정' : '새 게시글 작성'}
                </h3>
                <button
                    type="button"
                    className="post-form-close"
                    onClick={onCancel}
                    aria-label="닫기"
                >
                    <FaTimes size={18} />
                </button>
            </div>

            <form onSubmit={handleSubmit}>
                {/* 카테고리 & 제목을 한 줄에 */}
                <div className="post-form-row">
                    {/* 카테고리 */}
                    <div className="post-form-category">
                        <select
                            className={`post-form-input ${errors.category ? 'is-invalid' : ''}`}
                            value={category}
                            onChange={(e) => {
                                setCategory(e.target.value);
                                if (errors.category) {
                                    setErrors((prev) => ({ ...prev, category: null }));
                                }
                            }}
                            disabled={submitting}
                            required
                        >
                            {CATEGORY_OPTIONS.map((option) => (
                                <option
                                    key={option.value}
                                    value={option.value}
                                    disabled={option.value === ''}
                                    hidden={option.value === ''}
                                >
                                    {option.label}
                                </option>
                            ))}
                        </select>
                        {errors.category && (
                            <div className="invalid-feedback d-block">
                                {errors.category}
                            </div>
                        )}

                        <div className="d-flex gap-4 mt-3">
                            <div className="form-check form-switch">
                                <input
                                    className="form-check-input"
                                    type="checkbox"
                                    id="visibilityCheck"
                                    checked={visibility}
                                    onChange={(e) => {
                                        console.log('Visibility changed to:', e.target.checked);
                                        setVisibility(e.target.checked);
                                    }}
                                    disabled={submitting}
                                    style={{ cursor: 'pointer' }}
                                />
                                <label className="form-check-label" htmlFor="visibilityCheck" style={{ cursor: 'pointer', userSelect: 'none' }}>
                                    {visibility ? '공개' : '비공개'}
                                </label>
                            </div>

                            <div className="form-check form-switch">
                                <input
                                    className="form-check-input"
                                    type="checkbox"
                                    id="commentableCheck"
                                    checked={commentable}
                                    onChange={(e) => {
                                        console.log('Commentable changed to:', e.target.checked);
                                        setCommentable(e.target.checked);
                                    }}
                                    disabled={submitting}
                                    style={{ cursor: 'pointer' }}
                                />
                                <label className="form-check-label" htmlFor="commentableCheck" style={{ cursor: 'pointer', userSelect: 'none' }}>
                                    {commentable ? '댓글 허용' : '댓글 비허용'}
                                </label>
                            </div>
                        </div>
                    </div>

                    {/* 제목 */}
                    <div className="post-form-title-input">
                        <input
                            type="text"
                            className={`post-form-input ${errors.title ? 'is-invalid' : ''}`}
                            placeholder="제목을 입력하세요"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            maxLength={100}
                            disabled={submitting}
                        />
                        {errors.title && (
                            <div className="invalid-feedback d-block">
                                {errors.title}
                            </div>
                        )}
                    </div>
                </div>

                {/* 내용 */}
                <div className="post-form-content">
                    <textarea
                        className={`post-form-input post-form-textarea ${errors.content ? 'is-invalid' : ''}`}
                        placeholder="내용을 입력하세요"
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        maxLength={5000}
                        disabled={submitting}
                        rows={6}
                    />
                    {errors.content && (
                        <div className="invalid-feedback d-block">
                            {errors.content}
                        </div>
                    )}
                    <div className="post-form-char-count">
                        <span className={content.length > 4500 ? 'text-warning' : ''}>
                            {content.length.toLocaleString()}
                        </span>
                        <span> / 5,000</span>
                    </div>
                </div>

                {/* 에러 메시지 */}
                {errors.submit && (
                    <div className="alert alert-danger py-2 mt-3">
                        {errors.submit}
                    </div>
                )}

                {/* 푸터 - 버튼 */}
                <div className="post-form-footer">
                    <button
                        type="button"
                        className="post-form-btn post-form-btn-cancel"
                        onClick={onCancel}
                        disabled={submitting}
                    >
                        취소
                    </button>
                    <button
                        type="submit"
                        className="post-form-btn post-form-btn-submit"
                        disabled={submitting || !isFormValid}
                    >
                        {submitting ? (
                            <span className="spinner-border spinner-border-sm me-2" />
                        ) : (
                            <FaPaperPlane className="me-2" />
                        )}
                        {isEditing ? '수정하기' : '게시하기'}
                    </button>
                </div>
            </form>
        </div>
    );
}

PostForm.propTypes = {
    post: PropTypes.shape({
        id: PropTypes.number,
        title: PropTypes.string,
        content: PropTypes.string,
        category: PropTypes.string,
        visibility: PropTypes.bool,
        commentable: PropTypes.bool
    }),
    onSubmit: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired
};

export default PostForm;


