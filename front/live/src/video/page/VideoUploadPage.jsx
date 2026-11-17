import { useNavigate } from "react-router-dom";
import VideoUploadForm from "../form/VideoUploadForm";
import { useState } from "react";
import { Button } from "react-bootstrap";
import VideoUploadModal from "../form/VideoUploadModal";

/**
 * 동영상 업로드 페이지
 * 먼저 동영상 정보를 등록하고, 미리 서명된 URL을 사용하여 Cloudflare에 직접 업로드
 */
function VideoUploadPage() {
    const navigate = useNavigate();
    const [isUploading, setIsUploading] = useState(false);
    const [videoData, setVideoData] = useState(null);

    const handleFormComplete = (data) => {
        setVideoData(data);
        setIsUploading(true);
    };

    const handleSuccessUpload = async (videoId) => {
        // 이미 업로드 완료 및 검증을 VideoUploadService 메소드 내에서 진행하므로 
        // 검증 API 호출 삭제(2026/11/18)
        navigate("/channel/my_channel", {
            state: { uploadSubmittedVideoId: videoId },
        });
    };

    return (
        <div className="video-upload-page">
            <h1>동영상 업로드</h1>
            <VideoUploadForm onSuccess={handleFormComplete} />
            <div className="mt-3">
                <Button variant="secondary" onClick={() => navigate("/channel/my_channel")}>
                    채널로 돌아가기
                </Button>
            </div>
            <VideoUploadModal
                show={isUploading}
                onHide={() => setIsUploading(false)}
                videoData={videoData}
                onSuccess={handleSuccessUpload}
            />
        </div>
    );
}

export default VideoUploadPage;