import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import ApiService from '../../common/api/ApiService';
import './Cover.css';

/**
 * Cover 컴포넌트 - YouTube 스타일의 채널 배너 이미지
 * @param {string} channelName - 채널 이름 (커버 이미지 조회용)
 * @param {boolean} isEditable - 편집 가능 여부 (내 채널일 경우 true)
 * @param {function} onEditClick - 편집 버튼 클릭 핸들러
 */
function Cover({ channelName, isEditable = false, onEditClick }) {
    const [imageUrl, setImageUrl] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!channelName) {
            setLoading(false);
            return;
        }

        const fetchCover = async () => {
            setLoading(true);

            try {
                // 이미지 URL 조회 - profile_image와 동일한 방식
                const response = await ApiService.cover.read_image(channelName);
                const data = response.data;

                if (data && data.image_url) {
                    setImageUrl(data.image_url);
                } else {
                    setImageUrl(null);
                }
            } catch (err) {
                console.error('커버 이미지 로딩 실패:', err);
                // 커버가 없는 경우 기본 그라데이션 표시
                setImageUrl(null);
            } finally {
                setLoading(false);
            }
        };

        fetchCover();
    }, [channelName]);

    // 기본 그라데이션 배경 (커버 이미지가 없을 때)
    const defaultGradient = 'linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%)';

    return (
        <div className="cover-container">
            {/* 커버 이미지 영역 */}
            <div
                className="cover-banner"
                style={{
                    backgroundImage: imageUrl
                        ? `url(${imageUrl})`
                        : defaultGradient,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    backgroundRepeat: 'no-repeat'
                }}
            >
                {/* 오버레이 그라데이션 */}
                <div className="cover-overlay"></div>

                {/* 편집 버튼 (내 채널일 경우만 표시) */}
                {isEditable && (
                    <button
                        className="cover-edit-btn"
                        onClick={onEditClick}
                        aria-label="커버 이미지 편집"
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="16"
                            height="16"
                            fill="currentColor"
                            viewBox="0 0 16 16"
                        >
                            <path d="M12.854.146a.5.5 0 0 0-.707 0L10.5 1.793 14.207 5.5l1.647-1.646a.5.5 0 0 0 0-.708l-3-3zm.646 6.061L9.793 2.5 3.293 9H3.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.207l6.5-6.5zm-7.468 7.468A.5.5 0 0 1 6 13.5V13h-.5a.5.5 0 0 1-.5-.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.5-.5V10h-.5a.499.499 0 0 1-.175-.032l-.179.178a.5.5 0 0 0-.11.168l-2 5a.5.5 0 0 0 .65.65l5-2a.5.5 0 0 0 .168-.11l.178-.178z" />
                        </svg>
                        <span>커버 편집</span>
                    </button>
                )}

                {/* 로딩 상태 */}
                {loading && (
                    <div className="cover-loading">
                        <div className="cover-spinner"></div>
                    </div>
                )}
            </div>
        </div>
    );
}

Cover.propTypes = {
    channelName: PropTypes.string,
    isEditable: PropTypes.bool,
    onEditClick: PropTypes.func
};

export default Cover;
