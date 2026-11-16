import { Form, Button } from "react-bootstrap";
import { useState } from "react";

const CATEGORIES = JSON.parse(import.meta.env.VITE_VIDEO_CATEGORIES || "[]");
/**
 * VideoUploadForm 컴포넌트 - 동영상 업로드 폼
 * @param {function} onSuccess - 폼 제출 성공 시 동영상 데이터를 전달할 콜백 함수
 */
function VideoUploadForm({ onSuccess }) {
    const [video, setVideo] = useState({
        title: "",
        description: "",
        category: "ETC",
        visibility: "PRIVATE",
        allow_comments: true,
        duration_seconds: 0
    });
    const [error, setError] = useState("");

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setVideo((prev) => ({ ...prev, [name]: type === "checkbox" ? checked : value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!video.title.trim()) {
            setError("제목을 입력해주세요.");
            return;
        }
        setError("");
        // 실제 파일의 크기·확장자·길이는 다음 단계에서 결정한다.
        onSuccess?.({ ...video, title: video.title.trim(), description: video.description.trim() });
    };

    return (
        <Form onSubmit={handleSubmit}>
            <p className="text-muted small">
                파일 형식과 크기는 다음 단계에서 확인합니다. MP4/MOV 형식과 최대 10분 영상을 권장합니다.
            </p>
            {error && <div role="alert" className="text-danger mb-3">{error}</div>}

            <Form.Group className="mb-3" controlId="uploadVideoTitle">
                <Form.Label>제목</Form.Label>
                <Form.Control name="title" maxLength={200} required value={video.title}
                    onChange={handleInputChange} placeholder="영상 제목" />
            </Form.Group>


            <Form.Group className="mb-3" controlId="uploadVideoDescription">
                <Form.Label>설명</Form.Label>
                <Form.Control as="textarea" rows={3} name="description" value={video.description}
                    onChange={handleInputChange} placeholder="영상 설명 (선택)" />
            </Form.Group>

            <Form.Group className="mb-3" controlId="uploadVideoCategory">
                <Form.Label>카테고리</Form.Label>
                <Form.Select name="category" value={video.category} onChange={handleInputChange}>
                    {CATEGORIES.map(([value, label]) => (
                        <option key={value} value={value}>{label}</option>
                    ))}
                </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3" controlId="uploadVideoVisibility">
                <Form.Label>공개 범위</Form.Label>
                <Form.Select name="visibility" value={video.visibility} onChange={handleInputChange}>
                    <option value="PRIVATE">비공개</option>
                    <option value="PUBLIC">전체 공개</option>
                </Form.Select>
                <Form.Text className="text-muted">
                    CDN이 공개 버킷에 연결되어 있다면 비공개 영상 보호에는 별도의 서버/Worker 접근 제어가 필요합니다.
                </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3" controlId="uploadVideoComments">
                <Form.Check name="allow_comments" type="checkbox" label="댓글 허용"
                    checked={video.allow_comments} onChange={handleInputChange} />
            </Form.Group>

            <Button variant="primary" type="submit">다음: 동영상 파일 선택</Button>
        </Form>
    );
}

export default VideoUploadForm;