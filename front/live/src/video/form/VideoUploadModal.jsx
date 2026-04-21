import { Button, Modal, ProgressBar, Form } from "react-bootstrap";
import { useState, useRef, useEffect } from "react";
import { FaFileVideo, FaCheckCircle, FaExclamationCircle } from "react-icons/fa";
import "./VideoUploadModal.css";
import VideoUploadService from "../service/VideoUploadService";

/**
 * @param {boolean} show - 모달 표시 여부
 * @param {function} onHide - 모달 닫기
 * @param {Object} videoData - 이전 폼에서 입력받은 동영상 메타데이터
 * @param {function} onSuccess - 동영상 업로드 성공 시 호출될 콜백 (videoId 전달)
 */
function VideoUploadModal({ show, onHide, videoData, onSuccess }) {
    const [file, setFile] = useState(null);
    const [progress, setProgress] = useState(0);
    const [status, setStatus] = useState("idle");
    const [errorMessage, setErrorMessage] = useState("");
    const fileInputRef = useRef(null);

    // 모달이 닫힐 때 상태 매번 초기화
    useEffect(() => {
        if (!show) {
            setFile(null);
            setProgress(0);
            setStatus("idle");
            setErrorMessage("");
        }
    }, [show]);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            setFile(selectedFile);
            setErrorMessage("");
        }
    };

    const handleStartUpload = async () => {
        if (!file) return;

        try {
            setStatus("uploading");
            setProgress(0);

            // VideoUploadService를 사용하여 파일 크기에 따른 업로드 처리
            const response = await VideoUploadService.upload(file, videoData, (percent) => {
                setProgress(percent);
            });

            if (response.status === 200 || response.status === 201) {
                setStatus("success");
                if (onSuccess) {
                    onSuccess(response.video_id);
                }
            } else {
                throw new Error("업로드 실패: 서버 응답 오류");
            }
        } catch (error) {
            console.error("Upload error:", error);
            setStatus("error");
            setErrorMessage(error.response?.data?.message || error.message || "동영상 업로드 중 오류가 발생했습니다.");
        }
    };

    const formatBytes = (bytes, decimals = 2) => {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const dm = decimals < 0 ? 0 : decimals;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
    };

    return (
        <Modal show={show} onHide={status === "uploading" ? null : onHide} className="video-upload-modal" centered backdrop="static">
            <Modal.Header closeButton={status !== "uploading"}>
                <Modal.Title>동영상 파일 업로드</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="upload-container">
                    {status === "idle" && (
                        <div className="file-selector-container text-center w-100">
                            <Form.Group controlId="formFile" className="mb-3">
                                <div
                                    className="drop-zone py-5 border border-dashed rounded-3 cursor-pointer"
                                    onClick={() => fileInputRef.current?.click()}
                                    style={{ border: '2px dashed rgba(255,255,255,0.2)', cursor: 'pointer', transition: '0.3s' }}
                                    onMouseOver={(e) => e.currentTarget.style.borderColor = '#3b82f6'}
                                    onMouseOut={(e) => e.currentTarget.style.borderColor = 'rgba(255,255,255,0.2)'}
                                >
                                    <FaFileVideo size={48} className="text-primary mb-3" />
                                    <p className="mb-1 fw-bold">동영상 파일을 선택하거나 드래그하세요</p>
                                    <p className="small text-muted mb-0">mp4, mov, avi (최대 1GB)</p>
                                </div>
                                <Form.Control
                                    type="file"
                                    accept="video/*"
                                    onChange={handleFileChange}
                                    ref={fileInputRef}
                                    style={{ display: "none" }}
                                />
                            </Form.Group>

                            {file && (
                                <div className="file-info fade-in">
                                    <FaFileVideo className="file-icon" />
                                    <div className="file-details text-start">
                                        <h5>{file.name}</h5>
                                        <span>{formatBytes(file.size)}</span>
                                    </div>
                                </div>
                            )}
                        </div>
                    )}

                    {(status === "uploading" || status === "success" || status === "error") && (
                        <div className="progress-container fade-in">
                            {status === "uploading" && (
                                <>
                                    <div className="status-text">
                                        <span>업로드 중...</span>
                                        <span>{progress}%</span>
                                    </div>
                                    <ProgressBar now={progress} animated />
                                    <div className="text-center mt-3 text-muted small">
                                        파일 전송이 완료될 때까지 브라우저 창을 닫지 마세요.
                                    </div>
                                </>
                            )}

                            {status === "success" && (
                                <div className="success-animation">
                                    <FaCheckCircle className="check-icon" />
                                    <h3>업로드 완료!</h3>
                                    <p className="text-muted">동영상이 성공적으로 전송되었습니다.</p>
                                </div>
                            )}

                            {status === "error" && (
                                <div className="error-container text-center">
                                    <FaExclamationCircle className="text-danger mb-3" size={48} />
                                    <h4 className="text-danger">업로드 오류</h4>
                                    <p className="text-muted">{errorMessage}</p>
                                    <Button variant="outline-primary" onClick={() => setStatus("idle")}>
                                        다시 시도
                                    </Button>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </Modal.Body>
            <Modal.Footer>
                {status === "idle" && (
                    <>
                        <Button variant="secondary" onClick={onHide}>
                            취소
                        </Button>
                        <Button
                            variant="primary"
                            disabled={!file || !videoData}
                            onClick={handleStartUpload}
                        >
                            업로드 시작
                        </Button>
                    </>
                )}
                {(status === "success" || status === "error") && (
                    <Button variant="primary" onClick={onHide}>
                        닫기
                    </Button>
                )}
            </Modal.Footer>
        </Modal>
    );
}

export default VideoUploadModal;