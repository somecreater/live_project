import React, { useState, useEffect, useCallback } from 'react';
import PropTypes from 'prop-types';
import { FaPlus, FaNewspaper, FaChevronDown } from 'react-icons/fa';
import Post from './Post';
import PostForm from '../form/PostForm';
import PostSearchTab from './PostSearchTab';
import PostDetail from './PostDetail';
import ApiService from '../../common/api/ApiService';
import './Post.css';

/**
 * PostList 컴포넌트 - 게시글 목록
 * @param {string} channelName - 채널 이름 (검색용)
 * @param {boolean} isOwner - 채널 소유주 여부 (글쓰기/수정/삭제 권한)
 */
function PostList({ channelName, isOwner = false }) {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showWriteForm, setShowWriteForm] = useState(false);
    const [editingPost, setEditingPost] = useState(null);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [searchType, setSearchType] = useState('');
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [totalCount, setTotalCount] = useState(0);

    // 상세보기 모달 상태
    const [showDetail, setShowDetail] = useState(false);
    const [selectedPost, setSelectedPost] = useState(null);

    // 게시글 목록 조회
    const fetchPosts = useCallback(async (pageNum, isLoadMore = false) => {
        if (!channelName) return;

        try {
            if (!isLoadMore) {
                setLoading(true);
            }

            console.log(`Fetching posts for ${channelName}, owner: ${isOwner}`);

            // PostSearchRequest 스펙에 맞게 파라미터 구성
            const searchParams = {
                channel_name: channelName,
                keyword: searchKeyword || '',
                type: searchType,
                page: pageNum,
                size: 10
            };

            const response = await ApiService.post.list(searchParams);

            if (response.data && response.data.result) {
                const postPage = response.data.post_page;
                const newPosts = postPage?.content || [];

                if (isLoadMore) {
                    setPosts(prev => [...prev, ...newPosts]);
                } else {
                    console.log('Fetched posts:', newPosts);
                    setPosts(newPosts);
                }

                setTotalCount(postPage?.totalElements || newPosts.length);
                setHasMore(!postPage?.last && newPosts.length > 0);
            } else {
                // 결과가 없거나 실패 시
                if (!isLoadMore) setPosts([]);
                setHasMore(false);
            }
        } catch (err) {
            console.error('게시글 목록 로딩 실패:', err);
            setError('게시글을 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    }, [channelName, searchKeyword, searchType]);

    useEffect(() => {
        setPage(0);
        fetchPosts(0, false);
    }, [channelName, searchKeyword, searchType, fetchPosts]);

    // 더보기 클릭
    const handleLoadMore = () => {
        const nextPage = page + 1;
        setPage(nextPage);
        fetchPosts(nextPage, true);
    };

    // 게시글 작성 완료
    const handlePostSubmit = async (postData) => {
        try {
            if (editingPost) {
                const updateDto = {
                    id: editingPost.id,
                    channel_name: editingPost.channel_name,
                    title: postData.title,
                    content: postData.content,
                    category: postData.category,
                    visibility: postData.visibility,
                    commentable: postData.commentable
                };
                console.log('Sending update request:', updateDto);
                await ApiService.post.update(updateDto);
            } else {
                // 신규 작성: 채널명만 추가 (나머지는 postData에 포함됨)
                const createDto = {
                    ...postData,
                    channel_name: channelName
                };
                console.log('Sending write request with:', createDto);
                await ApiService.post.write(createDto);
            }

            setShowWriteForm(false);
            setEditingPost(null);
            fetchPosts(); // 목록 새로고침
        } catch (err) {
            console.error('게시글 저장 실패:', err);
            throw err;
        }
    };

    // 게시글 삭제
    const handleDelete = async (post) => {
        if (!window.confirm('정말 이 게시글을 삭제하시겠습니까?')) return;

        try {
            await ApiService.post.delete({
                channel_name: channelName,
                post_id: post.id
            });
            setShowDetail(false);
            setSelectedPost(null);
            fetchPosts();
        } catch (err) {
            console.error('게시글 삭제 실패:', err);
            alert('게시글 삭제에 실패했습니다.');
        }
    };

    // 게시글 수정
    const handleEdit = (post) => {
        setShowDetail(false);
        setSelectedPost(null);
        setEditingPost(post);
        setShowWriteForm(true);
    };

    // 게시글 상세보기
    const handleDetail = (post) => {
        setSelectedPost(post);
        setShowDetail(true);
    };

    // 상세 모달 닫기
    const handleDetailClose = () => {
        setShowDetail(false);
        setSelectedPost(null);
    };

    // 검색
    const handleSearch = (keyword, type) => {
        setSearchKeyword(keyword);
        setSearchType(type);
        setPage(0);
    };

    // 폼 닫기
    const handleFormClose = () => {
        setShowWriteForm(false);
        setEditingPost(null);
    };

    // 로딩 상태
    if (loading && posts.length === 0) {
        return (
            <div className="post-list-container">
                <div className="post-loading">
                    <div className="post-spinner"></div>
                </div>
            </div>
        );
    }

    return (
        <div className="post-list-container">
            {/* 헤더 */}
            <div className="post-list-header">
                <h2 className="post-list-title">
                    <FaNewspaper />
                    게시글
                    <span className="post-count-badge">{totalCount}</span>
                </h2>

                {/* 채널 소유주만 글쓰기 버튼 표시 */}
                {isOwner && (
                    <button
                        className="write-post-btn"
                        onClick={() => setShowWriteForm(true)}
                    >
                        <FaPlus />
                        글쓰기
                    </button>
                )}
            </div>

            {/* 검색 탭 */}
            <PostSearchTab
                onSearch={handleSearch}
                keyword={searchKeyword}
                searchType={searchType}
            />

            {/* 글쓰기 폼 */}
            {showWriteForm && (
                <PostForm
                    post={editingPost}
                    onSubmit={handlePostSubmit}
                    onCancel={handleFormClose}
                />
            )}

            {/* 에러 메시지 */}
            {error && (
                <div className="alert alert-danger" role="alert">
                    {error}
                </div>
            )}

            {/* 게시글 목록 */}
            {posts.length === 0 ? (
                <div className="post-empty-state">
                    <div className="post-empty-icon">
                        <FaNewspaper />
                    </div>
                    <h3 className="post-empty-title">게시글이 없습니다</h3>
                    <p className="post-empty-text">
                        {isOwner
                            ? '첫 번째 게시글을 작성해보세요!'
                            : '아직 작성된 게시글이 없습니다.'
                        }
                    </p>
                    {isOwner && (
                        <button
                            className="write-post-btn"
                            onClick={() => setShowWriteForm(true)}
                        >
                            <FaPlus />
                            첫 게시글 작성
                        </button>
                    )}
                </div>
            ) : (
                <>
                    {posts.map((post) => (
                        <Post
                            key={post.id}
                            post={post}
                            isOwner={isOwner}
                            onDetail={handleDetail}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                        />
                    ))}

                    {/* 더보기 버튼 */}
                    {hasMore && (
                        <div className="text-center mt-4 pb-4">
                            <button
                                className="load-more-btn"
                                onClick={handleLoadMore}
                            >
                                <FaChevronDown />
                                더보기
                            </button>
                        </div>
                    )}
                </>
            )}

            {/* 상세보기 모달 */}
            {showDetail && (
                <PostDetail
                    show={showDetail}
                    post={selectedPost}
                    isOwner={isOwner}
                    onHide={handleDetailClose}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                />
            )}
        </div>
    );
}

PostList.propTypes = {
    channelName: PropTypes.string.isRequired,
    isOwner: PropTypes.bool
};

export default PostList;




