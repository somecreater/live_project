import { Button, Modal, ProgressBar, Form } from "react-bootstrap";
import { useState, useRef, useEffect } from "react";
import { FaFileVideo, FaCheckCircle, FaExclamationCircle } from "react-icons/fa";
import "./VideoUploadModal.css";
import VideoUploadService from "../service/VideoUploadService";

const MAX_SIZE_BYTES = Number(import.meta.env.VITE_MAX_VIDEO_SIZE_BYTES || 10 * 1024 ** 3);
const MAX_DURATION_SECONDS = Number(import.meta.env.VITE_MAX_VIDEO_DURATION_SECONDS || 600);

function formatBytes(bytes) {
    if (!bytes) return "0 B";
    const units = ["B", "KB", "MB", "GB", "TB"];
    const index = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), units.length - 1);
    return `${(bytes / 1024 ** index).toFixed(index ? 2 : 0)} ${units[index]}`;
}

/** 브라우저에서 읽을 수 있을 때만 영상 길이를 추출한다 (MOV 등 일부 파일은 미지원). */
function readVideoDuration(file) {
    return new Promise((resolve) => {
        const objectUrl = URL.createObjectURL(file);
        const video = document.createElement("video");
        let resolved = false;
        const finish = (duration = 0) => {
            if (resolved) return;
            resolved = true;
            clearTimeout(timer);
            video.removeAttribute("src");
            video.load();
            URL.revokeObjectURL(objectUrl);
            resolve(duration);
        };
        const timer = setTimeout(() => finish(), 7000);
        video.preload = "metadata";
        video.onloadedmetadata = () => finish(Number.isFinite(video.duration) ? Math.ceil(video.duration) : 0);
        video.onerror = () => finish();
        video.src = objectUrl;
    });
}

const PHASE_LABELS = {
    preparing: "업로드 준비 중...",
    uploading: "파일 전송 중...",
    finalizing: "R2 업로드 완료 처리 중...",
    validating: "서버에서 파일 검증 중...",
    accepted: "파일 검증 완료",
    cancelling: "업로드 취소 중...",
};

/**
 * @param {boolean} show - 모달 표시 여부
 * @param {function} onHide - 모달 닫기
 * @param {Object} videoData - 이전 폼에서 입력받은 동영상 메타데이터
 * @param {function} onSuccess - 동영상 업로드 성공 시 호출될 콜백 (videoId 전달)
 */
function VideoUploadModal({ show, onHide, videoData, onSuccess }) {
    const [file, setFile] = useState(null);
    const [duration, setDuration] = useState(0);
    const [progress, setProgress] = useState(0);
    const [phase, setPhase] = useState("idle");
    const [status, setStatus] = useState("idle");
    const [errorMessage, setErrorMessage] = useState("");
    const [uploadedBytes, setUploadedBytes] = useState(0);
    const fileInputRef = useRef(null);
    const abortRef = useRef(null);
    const fileSelectionRef = useRef(0);

    // 모달이 닫힐 때 상태 매번 초기화
    useEffect(() => {
        if (!show) {
            fileSelectionRef.current += 1;
            setFile(null);
            setDuration(0);
            setProgress(0);
            setUploadedBytes(0);
            setStatus("idle");
            setPhase("idle");
            setErrorMessage("");
        }
    }, [show]);

    const handleSelectedFile = async (candidate) => {
        if (!candidate) return;
        const selectionId = ++fileSelectionRef.current;
        const extension = candidate.name.split(".").pop()?.toLowerCase();
        if (!["mp4", "mov"].includes(extension)) {
            setFile(null);
            setErrorMessage("MP4 또는 MOV 파일을 선택해주세요.");
            return;
        }
        if (candidate.size === 0 || candidate.size > MAX_SIZE_BYTES) {
            setFile(null);
            setErrorMessage(`파일 크기는 0B 초과, ${formatBytes(MAX_SIZE_BYTES)} 이하여야 합니다.`);
            return;
        }

        setErrorMessage("");
        setFile(candidate);
        setDuration(0);
        const seconds = await readVideoDuration(candidate);
        if (selectionId !== fileSelectionRef.current) return;
        if (seconds > MAX_DURATION_SECONDS) {
            setFile(null);
            setErrorMessage("영상 길이는 10분 이하여야 합니다.");
            return;
        }
        setDuration(seconds);
    };

    const handleStartUpload = async () => {
        if (!file || !videoData || status !== "idle") return;
        const controller = new AbortController();
        abortRef.current = controller;
        setProgress(0);
        setUploadedBytes(0);
        setErrorMessage("");
        setPhase("preparing");
        setStatus("working");

        try {
            // VideoUploadService를 사용하여 파일 크기에 따른 업로드 처리
            const result = await VideoUploadService.upload(
                file,
                { ...videoData, duration_seconds: duration },
                ({ phase: nextPhase, percent, uploadedBytes: bytes }) => {
                    setPhase(nextPhase);
                    setProgress(percent ?? 0);
                    setUploadedBytes(bytes ?? 0);
                },
                { signal: controller.signal }
            );
            setStatus("success");
            setPhase("accepted");
            onSuccess?.(result.videoId);
        } catch (error) {
            if (VideoUploadService.isCanceled(error, controller.signal)) {
                setStatus("cancelled");
                setErrorMessage("사용자가 업로드를 취소했습니다.");
            } else {
                console.error("동영상 업로드 오류:", error);
                setStatus("error");
                setErrorMessage(error.message || "업로드 중 문제가 발생했습니다.");
            }
        } finally {
            abortRef.current = null;
        }
    };

    const isWorking = status === "working";
    const canCancel = isWorking && ["preparing", "uploading", "cancelling"].includes(phase);


    const cancelUpload = () => {
        if (!canCancel || !abortRef.current) return;
        setPhase("cancelling");
        abortRef.current.abort();
    };

    const reset = () => {
        setProgress(0);
        setUploadedBytes(0);
        setErrorMessage("");
        setPhase("idle");
        setStatus("idle");
    };

    return (
        <Modal show={show} onHide={isWorking ? undefined : onHide}
            centered backdrop="static" className="video-upload-modal">
            <Modal.Header closeButton={!isWorking}>
                <Modal.Title>동영상 파일 업로드</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {status === "idle" && (
                    <div className="file-selector-container text-center w-100">
                        <div className="drop-zone py-5" role="button" tabIndex={0}
                            onClick={() => fileInputRef.current?.click()}
                            onKeyDown={(event) => {
                                if (event.key === "Enter" || event.key === " ") {
                                    event.preventDefault();
                                    fileInputRef.current?.click();
                                }
                            }}
                            onDragOver={(event) => event.preventDefault()}
                            onDrop={(event) => {
                                event.preventDefault();
                                void handleSelectedFile(event.dataTransfer.files?.[0]);
                            }}>
                            <FaFileVideo size={48} className="file-icon mb-3" />
                            <p className="mb-1 fw-bold">파일 선택 또는 드래그하여 업로드</p>
                            <p className="small mb-0">MP4 / MOV · 최대 {formatBytes(MAX_SIZE_BYTES)} · 10분 이내</p>
                        </div>
                        <Form.Control type="file" accept=".mp4,.mov,video/mp4,video/quicktime"
                            ref={fileInputRef} className="visually-hidden" aria-label="동영상 파일 선택"
                            onChange={(event) => { void handleSelectedFile(event.target.files?.[0]); }} />

                        {file && (
                            <div className="file-info">
                                <FaFileVideo className="file-icon" />
                                <div className="file-details text-start">
                                    <h5>{file.name}</h5>
                                    <span>{formatBytes(file.size)}{duration ? ` · ${Math.ceil(duration / 60)}분 이내` : " · 영상 길이 확인 불가(서버에서 최종 검증)"}</span>
                                </div>
                            </div>
                        )}
                        {errorMessage && <p role="alert" className="text-danger mt-3">{errorMessage}</p>}
                    </div>
                )}

                {isWorking && (
                    <div className="progress-container" role="status" aria-live="polite">
                        <div className="status-text"><span>{PHASE_LABELS[phase] ?? "처리 중..."}</span><span>{progress}%</span></div>
                        <ProgressBar now={progress} animated striped />
                        <div className="text-center mt-3 small">
                            {formatBytes(uploadedBytes)} / {formatBytes(file?.size || 0)}
                            <p className="mt-2 mb-0">파일 업로드 완료 후에도 검증과 트랜스코딩 처리가 이어집니다.</p>
                        </div>
                    </div>
                )}

                {status === "success" && (
                    <div className="success-animation">
                        <FaCheckCircle className="check-icon" />
                        <h3>업로드 접수 완료</h3>
                        <p>원본 파일 검증이 끝났습니다. 영상은 서버에서 트랜스코딩 중입니다.</p>
                    </div>
                )}

                {(status === "error" || status === "cancelled") && (
                    <div className="error-container text-center">
                        <FaExclamationCircle size={48} className="text-danger mb-3" />
                        <h4>{status === "cancelled" ? "업로드 취소" : "업로드 오류"}</h4>
                        <p role="alert">{errorMessage}</p>
                        <Button variant="outline-primary" onClick={reset}>다시 시도</Button>
                    </div>
                )}
            </Modal.Body>
            <Modal.Footer>
                {status === "idle" && (
                    <>
                        <Button variant="secondary" onClick={onHide}>닫기</Button>
                        <Button variant="primary" disabled={!file || !videoData} onClick={handleStartUpload}>업로드 시작</Button>
                    </>
                )}
                {canCancel && <Button variant="outline-danger" onClick={cancelUpload}>업로드 취소</Button>}
                {(status === "error" || status === "cancelled" || status === "success") &&
                    <Button variant="primary" onClick={onHide}>닫기</Button>}
            </Modal.Footer>
        </Modal>
    );
}

export default VideoUploadModal;