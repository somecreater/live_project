import { useNavigate } from "react-router-dom";
import ApiService from "../../common/api/ApiService";
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
    const [presignedUrl, setPresignedUrl] = useState("");
    const handleVideoUploadUrl = async (video) => {
        const response = await ApiService.video.upload_url(video);
        return response.data;
    };

    const handleSuccess = (data) => {
        setPresignedUrl(data.uploadUrl);
        setIsUploading(true);
        alert("동영상 정보가 성공적으로 업로드되었습니다.");
    };

    return (
        <div>
            <h1>Video Upload Page</h1>
            <VideoUploadForm onVideoUploadUrl={handleVideoUploadUrl} onSuccess={handleSuccess} />
            <Button variant="primary" onClick={() => navigate('/channel/my_channel')}>
                채널로 돌아가기
            </Button>
            <VideoUploadModal
                show={isUploading}
                onHide={() => setIsUploading(false)}
                onVideoUploadUrl={presignedUrl}
                onSuccess={() => {
                    alert("동영상이 성공적으로 업로드되었습니다.");
                    navigate('/channel/my_channel');
                }}
            />
        </div>
    );
}

export default VideoUploadPage;