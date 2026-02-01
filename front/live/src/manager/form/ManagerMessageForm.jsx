import { useState } from "react";
import useManagerApi from "../hooks/useManagerApi";

function ManagerMessageForm({ loading, error, targetId, setSendMessageModalOpen }) {
    const { sendMessage } = useManagerApi();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        const result = await sendMessage({ targetId, title, content });
        if (result) {
            alert('메시지 전송 성공');
            setSendMessageModalOpen(false);
        }
    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <label htmlFor="title">제목:</label>
                <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} />
                <label htmlFor="content">내용:</label>
                <input type="text" value={content} onChange={(e) => setContent(e.target.value)} />
                <label htmlFor="targetId">대상 ID:</label>
                <span>{targetId}</span>
                <button type="submit">Send</button>
            </form>
        </div>
    );
}

export default ManagerMessageForm;