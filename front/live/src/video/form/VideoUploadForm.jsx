import { Form, Button } from "react-bootstrap";
import { useState } from "react";

/**
 * VideoUploadForm 컴포넌트 - 동영상 업로드 폼
 * 먼저 동영상 정보를 임시로 DB에 저장하고, 미리 서명된 URL을 사용하여 Cloudflare에 직접 업로드
 * @param {function} onVideoUploadUrl - 업로드 성공 시 호출될 콜백 함수
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
        duration_seconds: 0,
    });
    const [file, setFile] = useState(null);
    const [isUploading, setIsUploading] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [error, setError] = useState(null);

    return (
        <Form>
            <Form.Group className="mb-3" controlId="formBasicTitle">
                <Form.Label>제목</Form.Label>
                <Form.Control type="text" placeholder="제목을 입력해주세요" />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicDescription">
                <Form.Label>설명</Form.Label>
                <Form.Control type="text" placeholder="설명을 입력해주세요" />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicFile">
                <Form.Label>파일</Form.Label>
                <Form.Control type="file" />
            </Form.Group>

            <Button variant="primary" type="submit">
                업로드
            </Button>
        </Form>
    );
}

export default VideoUploadForm;