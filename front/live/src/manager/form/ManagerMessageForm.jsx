import { useState } from "react";
import useManagerApi from "../hooks/useManagerApi";

function ManagerMessageForm({ targetId, setSendMessageModalOpen }) {
    const { sendMessage, loading, error } = useManagerApi();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [validationError, setValidationError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 입력 검증
        if (!title.trim()) {
            setValidationError('제목을 입력해주세요.');
            return;
        }
        if (!content.trim()) {
            setValidationError('내용을 입력해주세요.');
            return;
        }

        setValidationError('');

        const result = await sendMessage({ targetId, title, content });
        if (result) {
            alert('메시지 전송 성공');
            // 폼 초기화
            setTitle('');
            setContent('');
            setSendMessageModalOpen(false);
        }
    };

    return (
        <div className="manager-message-form">
            <form onSubmit={handleSubmit}>
                <div className="manager-form-group">
                    <label className="manager-label" htmlFor="message-title">제목</label>
                    <input
                        id="message-title"
                        className="manager-input"
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="메시지 제목을 입력하세요"
                        disabled={loading}
                        required
                    />
                </div>

                <div className="manager-form-group">
                    <label className="manager-label" htmlFor="message-content">내용</label>
                    <textarea
                        id="message-content"
                        className="manager-input"
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        placeholder="메시지 내용을 입력하세요"
                        rows="5"
                        disabled={loading}
                        required
                    />
                </div>

                <div className="manager-form-group">
                    <label className="manager-label">수신자</label>
                    <div className="manager-target-badge">{targetId}</div>
                </div>

                {validationError && (
                    <div className="manager-error-text">
                        {validationError}
                    </div>
                )}

                {error && (
                    <div className="manager-error-text">
                        전송 실패: {error}
                    </div>
                )}

                <button
                    type="submit"
                    className="btn-search"
                    style={{ width: '100%', marginTop: '1rem' }}
                    disabled={loading}
                >
                    {loading ? '전송 중...' : '메시지 전송'}
                </button>
            </form>
        </div>
    );
}

export default ManagerMessageForm;