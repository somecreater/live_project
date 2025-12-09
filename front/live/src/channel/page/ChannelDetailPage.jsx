import React, { useEffect, useState } from 'react';
import { Container, Spinner, Alert } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import Cover from '../component/Cover';
import ApiService from '../../common/api/ApiService';

function ChannelDetailPage() {
    const { id } = useParams(); // URL에서 채널 ID 추출
    const [channel, setChannel] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchChannel = async () => {
            if (!id) {
                setError('채널 ID가 필요합니다.');
                setLoading(false);
                return;
            }

            setLoading(true);
            setError(null);

            try {
                const response = await ApiService.channel.info(id);
                if (response.data && response.data.result) {
                    setChannel(response.data.channel);
                } else {
                    setError('채널 정보를 찾을 수 없습니다.');
                }
            } catch (err) {
                console.error('채널 정보 로딩 실패:', err);
                if (err.response?.status === 404) {
                    setError('존재하지 않는 채널입니다.');
                } else {
                    setError('채널 정보를 불러오는데 실패했습니다.');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchChannel();
    }, [id]);

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
        <div className="channel-detail-page">
            {/* 커버 이미지 - 다른 사람 채널이므로 편집 불가 */}
            <Cover
                channelName={channel?.name}
                isEditable={false}
            />

            {/* 채널 정보 영역 */}
            <Container className="mt-4">
                <div className="channel-header-info">
                    <h2>{channel?.name || '채널'}</h2>
                    {channel?.description && (
                        <p className="text-muted">{channel.description}</p>
                    )}
                    {channel?.user_login_id && (
                        <p className="text-secondary" style={{ fontSize: '0.9rem' }}>
                            @{channel.user_login_id}
                        </p>
                    )}
                </div>

                {/* 채널 콘텐츠 영역 (추후 확장) */}
                <div className="channel-content mt-4">
                    {/* 여기에 채널 콘텐츠 (동영상 목록 등) 추가 */}
                </div>
            </Container>
        </div>
    );
}

export default ChannelDetailPage;
