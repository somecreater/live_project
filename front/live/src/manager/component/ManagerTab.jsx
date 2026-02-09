
/**
 * 단순 관리자 탭 UI
 * @param {string} activeTab - 현재 활성화된 탭
 * @param {Function} setActiveTab - 탭 변경 함수
 */
function ManagerTab({
    activeTab,
    setActiveTab,
}) {
    return (
        <div>
            <button onClick={() => setActiveTab('USER')}>User</button>
            <button onClick={() => setActiveTab('CHANNEL')}>Channel</button>
            <button onClick={() => setActiveTab('VIDEO')}>Video</button>
            <button onClick={() => setActiveTab('POST')}>Post</button>
        </div>
    );
}

export default ManagerTab;