import { Form, Button } from "react-bootstrap";
import { useState } from "react";

/**
 * VideoUploadForm 컴포넌트 - 동영상 업로드 폼
 * @param {function} onSuccess - 폼 제출 성공 시 동영상 데이터를 전달할 콜백 함수
 */
function VideoUploadForm({ onSuccess }) {

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
    const [error, setError] = useState(null);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setVideo(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSuccess(video);
    };

    return (
        <Form onSubmit={handleSubmit}>
            <p style={{ color: "red" }}>
                특정 사이즈 이상의 동영상은 업로드 할 수 없습니다.(업로드 폼은 제공되지만 도중 업로드 실패)<br />
                동영상 길이는 10분 이내로 제한됩니다.
            </p>
            {error && <p style={{ color: "red" }}>{error}</p>}
            <Form.Group className="mb-3" controlId="formBasicTitle">
                <Form.Label>제목</Form.Label>
                <Form.Control type="text" name="title" value={video.title} onChange={handleInputChange} placeholder="제목을 입력해주세요" />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicDescription">
                <Form.Label>설명</Form.Label>
                <Form.Control type="text" name="description" value={video.description} onChange={handleInputChange} placeholder="설명을 입력해주세요" />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicFileType">
                <Form.Label>파일 타입</Form.Label>
                <Form.Select name="file_type" value={video.file_type} onChange={handleInputChange} aria-label="Default select example">
                    <option value="">파일 타입을 선택해주세요</option>
                    <option value="mp4">mp4</option>
                    <option value="mov">mov</option>
                </Form.Select>
            </Form.Group>

            <Button variant="primary" type="submit">
                동영상 정보 등록
            </Button>
        </Form>
    );
}

export default VideoUploadForm;