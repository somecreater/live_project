
import { FaUser, FaTv, FaVideo, FaFileAlt } from 'react-icons/fa';

/**
 * 단순 관리자 탭 UI
 * @param {string} activeTab - 현재 활성화된 탭
 * @param {Function} setActiveTab - 탭 변경 함수
 */
function ManagerTab({
    activeTab,
    setActiveTab,
}) {
    const tabs = [
        { id: 'USER', label: 'User', icon: <FaUser /> },
        { id: 'CHANNEL', label: 'Channel', icon: <FaTv /> },
        { id: 'VIDEO', label: 'Video', icon: <FaVideo /> },
        { id: 'POST', label: 'Post', icon: <FaFileAlt /> },
    ];

    return (
        <div className="manager-tabs">
            {tabs.map((tab) => (
                <button
                    key={tab.id}
                    className={`manager-tab-btn ${activeTab === tab.id ? 'active' : ''}`}
                    onClick={() => setActiveTab(tab.id)}
                >
                    {tab.icon}
                    {tab.label}
                </button>
            ))}
        </div>
    );
}

export default ManagerTab;
