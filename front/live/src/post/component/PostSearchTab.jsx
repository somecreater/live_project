import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { FaSearch } from 'react-icons/fa';
import './Post.css';

/**
 * PostSearchTab 컴포넌트 - 게시글 검색 및 필터 탭
 * @param {function} onSearch - 검색 핸들러
 * @param {string} keyword - 현재 검색 키워드
 * @param {string} searchType - 현재 검색 타입 (title, content, category)
 */
function PostSearchTab({ onSearch, keyword = '', searchType = 'title' }) {
    const [inputValue, setInputValue] = useState(keyword);
    const [activeTab, setActiveTab] = useState(searchType);

    const tabs = [
        { id: 'title', label: '제목' },
        { id: 'content', label: '내용' },
        { id: 'category', label: '카테고리' }
    ];

    // 검색 실행
    const handleSearch = (e) => {
        e?.preventDefault();
        onSearch?.(inputValue.trim(), activeTab);
    };

    // 탭 변경
    const handleTabChange = (tabId) => {
        setActiveTab(tabId);
        if (inputValue.trim()) {
            onSearch?.(inputValue.trim(), tabId);
        }
    };

    // Enter 키 처리
    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            handleSearch(e);
        }
    };

    return (
        <div className="post-search-container">
            {/* 검색 박스 */}
            <div className="post-search-box">
                <div className="post-search-input-wrapper">
                    <FaSearch className="post-search-icon" />
                    <input
                        type="text"
                        className="post-search-input"
                        placeholder="게시글 검색..."
                        value={inputValue}
                        onChange={(e) => setInputValue(e.target.value)}
                        onKeyDown={handleKeyDown}
                        aria-label="게시글 검색"
                    />
                </div>
                <button
                    className="post-search-btn"
                    onClick={handleSearch}
                    aria-label="검색"
                >
                    검색
                </button>
            </div>

            {/* 검색 타입 탭 */}
            <div className="post-tabs">
                {tabs.map((tab) => (
                    <button
                        key={tab.id}
                        className={`post-tab ${activeTab === tab.id ? 'active' : ''}`}
                        onClick={() => handleTabChange(tab.id)}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>
        </div>
    );
}

PostSearchTab.propTypes = {
    onSearch: PropTypes.func,
    keyword: PropTypes.string,
    searchType: PropTypes.string
};

export default PostSearchTab;

