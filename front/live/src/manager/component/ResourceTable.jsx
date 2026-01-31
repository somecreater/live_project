

const TABLE_CONFIG = {
    USER: {
        header: [
            { label: 'UID', accessor: 'id' },
            { label: '로그인 ID', accessor: 'loginId' },
            { label: '닉네임', accessor: 'nickname' },
            { label: '이메일', accessor: 'email' },
            { label: '로그인 타입', accessor: 'loginType' },
            { label: '유저 타입', accessor: 'userType' },
            { label: '전화번호', accessor: 'phone' },
            { label: '가입일', accessor: 'createdAt' },
            { label: '수정일', accessor: 'updatedAt' },
            { label: '삭제', accessor: 'delete' },
            { label: '메시지 전송', accessor: 'sendMessage' },
        ],
    },
    CHANNEL: {
        header: [
            { label: 'CID', accessor: 'id' },
            { label: '채널 이름', accessor: 'name' },
            { label: '소유자 로그인 ID', accessor: 'user_login_id' },
            { label: '설명', accessor: 'description' },
            { label: '구독자 수', accessor: 'subscription_count' },
            { label: '가입일', accessor: 'createdAt' },
            { label: '수정일', accessor: 'updatedAt' },
            { label: '삭제', accessor: 'delete' },
        ],
    },
    VIDEO: {
        header: [
            { label: 'VID', accessor: 'id' },
            { label: '제목', accessor: 'title' },
            { label: '설명', accessor: 'description' },
            { label: '파일 타입', accessor: 'file_type' },
            { label: '파일 크기', accessor: 'size' },
            { label: '공개여부', accessor: 'visibility' },
            { label: '댓글 허용여부', accessor: 'allow_comments' },
            { label: '동영상 상태', accessor: 'status' },
            { label: '재생 시간', accessor: 'duration_seconds' },
            { label: '좋아요 수', accessor: 'like' },
            { label: '싫어요 수', accessor: 'unlike' },
            { label: '조회수', accessor: 'view_count' },
            { label: '채널 이름', accessor: 'channel_name' },
            { label: '작성일', accessor: 'createdAt' },
            { label: '수정일', accessor: 'updatedAt' },
            { label: '삭제', accessor: 'delete' },
        ],
    },
    POST: {
        header: [
            { label: 'PID', accessor: 'id' },
            { label: '제목', accessor: 'title' },
            { label: '내용', accessor: 'content' },
            { label: '카테고리', accessor: 'category' },
            { label: '좋아요 수', accessor: 'like' },
            { label: '싫어요 수', accessor: 'unlike' },
            { label: '공개여부', accessor: 'visibility' },
            { label: '댓글 허용여부', accessor: 'commentable' },
            { label: '채널 이름', accessor: 'channel_name' },
            { label: '작성일', accessor: 'createdAt' },
            { label: '수정일', accessor: 'updatedAt' },
            { label: '삭제', accessor: 'delete' },
        ],
    }
}
/**
 * 관리자 테이블 컴포넌트
 * @param {string} resourceType - 리소스 타입 'USER' | 'CHANNEL' | 'VIDEO' | 'POST'
 * @param {Array} data - 테이블 데이터
 * @param {Function} onDelete - 삭제 함수
 * @param {Function} onSendMessage - 메시지 폼 호출 함수
 * @param {boolean} loading - 로딩 상태
 * @param {boolean} error - 에러 상태
 */
function ResourceTable({ resourceType, data, onDelete, onSendMessage, loading, error }) {

    return (
        <div>
            {loading && <p>로딩 중...</p>}
            {error && <p>에러: {error}</p>}
            {data && data.length > 0 && (
                <table>
                    <thead>
                        <tr>
                            {TABLE_CONFIG[resourceType].header.map((header) => (
                                <th key={header.accessor}>{header.label}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {data.map((item) => (
                            <tr key={item.id}>
                                {TABLE_CONFIG[resourceType].header.map((header) => (
                                    <td key={header.accessor}>{item[header.accessor]}</td>
                                ))}
                                {onDelete && (
                                    <td>
                                        <button onClick={() => onDelete(item.id)}>삭제</button>
                                    </td>
                                )}
                                {onSendMessage && resourceType === 'USER' && (
                                    <td>
                                        <button onClick={() => onSendMessage(item.loginId)}>메시지 전송</button>
                                    </td>
                                )}
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
            <ResourceTablePagenation />
        </div>
    );
}

export default ResourceTable;