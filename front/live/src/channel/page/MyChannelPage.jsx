import React, { useEffect, useState } from 'react';
import { Container, Spinner, Alert, Button, Modal } from 'react-bootstrap';
import Cover from '../component/Cover';
import ApiService from '../../common/api/ApiService';

function MyChannelPage() {
    const [channel, setChannel] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showCoverModal, setShowCoverModal] = useState(false);
    const [coverKey, setCoverKey] = useState(0); // 커버 리프레시용

    useEffect(() => {
        const fetchMyChannel = async () => {
            setLoading(true);
            setError(null);

            try {
                const response = await ApiService.channel.my_channel();
                if (response.data && response.data.result) {
                    setChannel(response.data.my_channel);
                } else {
                    setError('채널 정보를 찾을 수 없습니다.');
                }
            } catch (err) {
                console.error('채널 정보 로딩 실패:', err);
                setError('채널 정보를 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchMyChannel();
    }, []);

    const handleCoverEdit = () => {
        setShowCoverModal(true);
    };

    const handleCoverUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        try {
            await ApiService.cover.upload(formData);
            // 커버 컴포넌트 리프레시
            setCoverKey(prev => prev + 1);
            setShowCoverModal(false);
        } catch (err) {
            console.error('커버 업로드 실패:', err);
            alert('커버 이미지 업로드에 실패했습니다.');
        }
    };

    const handleCoverDelete = async () => {
        if (!window.confirm('정말 커버 이미지를 삭제하시겠습니까?')) return;

        try {
            await ApiService.cover.delete();
            // 커버 컴포넌트 리프레시
            setCoverKey(prev => prev + 1);
            setShowCoverModal(false);
        } catch (err) {
            console.error('커버 삭제 실패:', err);
            alert('커버 이미지 삭제에 실패했습니다.');
        }
    };

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
        <div className="my-channel-page">
            {/* 커버 이미지 */}
            <Cover
                key={coverKey}
                channelName={channel?.name}
                isEditable={true}
                onEditClick={handleCoverEdit}
            />

            {/* 채널 정보 영역 */}
            <Container className="mt-4">
                <div className="channel-info">
                    <h2>{channel?.name || '내 채널'}</h2>
                    {channel?.description && (
                        <p className="text-muted">{channel.description}</p>
                    )}
                </div>

                {/* 채널 콘텐츠 영역 (추후 확장) */}
                <div className="channel-content mt-4">
                    {/* 여기에 채널 콘텐츠 추가 */}
                </div>
            </Container>

            {/* 커버 편집 모달 */}
            <Modal show={showCoverModal} onHide={() => setShowCoverModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>커버 이미지 편집</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div className="d-grid gap-3">
                        <div>
                            <label
                                htmlFor="cover-upload"
                                className="btn btn-primary w-100"
                                style={{ cursor: 'pointer' }}
                            >
                                새 이미지 업로드
                            </label>
                            <input
                                id="cover-upload"
                                type="file"
                                accept="image/*"
                                onChange={handleCoverUpload}
                                style={{ display: 'none' }}
                            />
                        </div>
                        <Button variant="outline-danger" onClick={handleCoverDelete}>
                            커버 이미지 삭제
                        </Button>
                    </div>
                    <p className="text-muted mt-3 mb-0" style={{ fontSize: '0.85rem' }}>
                        권장 크기: 2560 x 423 픽셀<br />
                        파일 형식: JPG, PNG (최대 6MB)
                    </p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowCoverModal(false)}>
                        닫기
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default MyChannelPage;
