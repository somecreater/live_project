import { Form, Button } from "react-bootstrap";
import { useState } from "react";

/**
 * VideoUploadForm 컴포넌트 - 동영상 업로드 폼
 * 먼저 동영상 정보를 임시로 DB에 저장하고, 미리 서명된 URL을 사용하여 Cloudflare에 직접 업로드
 * @param {function} onVideoUploadUrl - 동영상 정보 업로드 성공 시 호출될 콜백 함수
 */
function VideoUploadForm({ onVideoUploadUrl, onSuccess }) {

    const [video, setVideo] = useState({
        id: "",
        title: "",
        description: "",
        file_type: "",
        size: 0,
        visibility: true,
        allow_comments: true,
        duration_seconds: 0
    });
    const [presignedUrl, setPresignedUrl] = useState("");
    const [isUploading, setIsUploading] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [error, setError] = useState(null);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setVideo(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsUploading(true);
        setError(null);
        const data = await onVideoUploadUrl(video);
        if (data.result) {
            setPresignedUrl(data.presigned_url);
            setVideo(data.video);
        } else {
            console.error("업로드 오류:", data);
            setError(data.message);
        }
        setIsUploading(false);
    };

    return (
        <Form onSubmit={handleSubmit}>
            <p style={{ color: "red" }}>
                특정 사이즈 이상의 동영상은 업로드 할 수 없습니다.(URL은 제공되지만 도중 업로드 실패)<br />
                동영상 길이는 10분 이내로 제한됩니다.
            </p>
            <Form.Group className="mb-3" controlId="formBasicTitle">
                <Form.Label>제목</Form.Label>
                <Form.Control type="text" value={video.title} onChange={handleInputChange} placeholder="제목을 입력해주세요" />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicDescription">
                <Form.Label>설명</Form.Label>
                <Form.Control type="text" value={video.description} onChange={handleInputChange} placeholder="설명을 입력해주세요" />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicFileType">
                <Form.Label>파일 타입</Form.Label>
                <Form.Select aria-label="Default select example">
                    <option>파일 타입을 선택해주세요</option>
                    <option value="mp4">mp4</option>
                    <option value="mov">mov</option>
                </Form.Select>
            </Form.Group>

            <Button variant="primary" type="submit">
                업로드 준비
            </Button>
        </Form>
    );
}

export default VideoUploadForm;