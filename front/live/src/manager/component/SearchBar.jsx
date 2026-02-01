const SEARCH_CONFIG = {
    USER: {
        searchType: [
            { value: 'loginId', label: '로그인 ID' },
            { value: 'nickname', label: '닉네임' },
            { value: 'email', label: '이메일' },
        ],
    },
    CHANNEL: {
        searchType: [
            { value: 'name', label: '채널 이름' },
            { value: 'user_login_id', label: '소유자 로그인 ID' },
        ],
    },
    VIDEO: {
        searchType: [
            { value: 'title', label: '제목' },
            { value: 'description', label: '설명' },
            { value: 'channel_name', label: '채널 이름' },
        ],
    },
    POST: {
        searchType: [
            { value: 'title', label: '제목' },
            { value: 'content', label: '내용' },
            { value: 'channel_name', label: '채널 이름' },
        ],
    },
}

/**
 * resourceType에 따라 검색 옵션을 제공하는 컴포넌트
 * @param {string} resourceType - 'USER' | 'CHANNEL' | 'VIDEO' | 'POST'
 * @param {string} searchType - 검색 타입
 * @param {string} keyword - 검색어
 * @param {Function} handleSearchChange - 검색어 및 검색 타입 변경 함수
 * @param {Function} onSearch - 검색 API 호출 함수
 * @param {Function} onClear - 검색 값 초기화 함수
 * @param {boolean} loading - 로딩 상태
 * @param {number} debounceMs - 디바운스 시간
 */
function SearchBar({
    resourceType,
    searchType,
    keyword,
    handleSearchChange,
    onSearch,
    onClear,
    loading,
    debounceMs = 300
}) {

    const options = SEARCH_CONFIG[resourceType]?.searchType || [];

    const handleSearchTypeChange = (e) => {
        handleSearchChange('searchType', e.target.value);
    }
    const handleKeywordChange = (e) => {
        handleSearchChange('keyword', e.target.value);
    }

    const submitSearchValue = (e) => {
        e.preventDefault();
        onSearch();
    }

    const clearSearchValue = () => {
        onClear();
    }

    return (
        <div className="flex items-center gap-2">
            <select value={searchType} onChange={handleSearchTypeChange}>
                {options.map((option) => (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>
            <input type="text" value={keyword} onChange={handleKeywordChange} />
            <button type="button" onClick={submitSearchValue}>검색</button>
            <button type="button" onClick={clearSearchValue}>
                초기화
            </button>
        </div>
    );
}

export default SearchBar;
