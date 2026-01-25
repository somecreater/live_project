import { Badge, Button, Dropdown } from "react-bootstrap";
import { managerMessageStore } from "../context/managerMessageStore";
import { userStateStore } from "../context/userStateStore";
import { FaEnvelope, FaTrash, FaTrashAlt } from "react-icons/fa";
import { useEffect } from "react";
import "./MessageCenter.css";

const MessageCenter = () => {
    const managermessages = managerMessageStore((state) => state.managermessages);
    const currentPage = managerMessageStore((state) => state.currentPage);
    const totalPages = managerMessageStore((state) => state.totalPages);
    const isLoadingMore = managerMessageStore((state) => state.isLoadingMore);
    const loadManagerMessages = managerMessageStore((state) => state.loadManagerMessages);
    const loadMoreManagerMessages = managerMessageStore((state) => state.loadMoreManagerMessages);
    const managerMessageRead = managerMessageStore((state) => state.managerMessageRead);
    const managerMessageReadAll = managerMessageStore((state) => state.managerMessageReadAll);
    const managerMessageDelete = managerMessageStore((state) => state.managerMessageDelete);
    const managerMessageDeleteAll = managerMessageStore((state) => state.managerMessageDeleteAll);

    const isAuthenticated = userStateStore((state) => state.isAuthenticated);

    useEffect(() => {
        if (isAuthenticated) {
            loadManagerMessages(0, 10, true);
        }
    }, [isAuthenticated, loadManagerMessages]);

    const displayManagerMessages = isAuthenticated ? managermessages : [];
    const sortedManagerMessages = [...displayManagerMessages];
    const unreadManagerMessageCount = isAuthenticated ? displayManagerMessages.filter(n => !n.read).length : 0;

    if (!isAuthenticated) {
        return null;
    }

    return (
        <Dropdown
            align="end"
            className="message-dropdown"
            onToggle={(isOpen) => {
                if (isOpen && unreadManagerMessageCount > 0) {
                    managerMessageReadAll();
                }
            }}
        >
            <Dropdown.Toggle as="div" className="message-toggle">
                <div className="message-container">
                    <FaEnvelope className="message-icon" />
                    {unreadManagerMessageCount > 0 && (
                        <Badge bg="danger" pill className="message-badge">
                            {unreadManagerMessageCount > 99 ? '99+' : unreadManagerMessageCount}
                        </Badge>
                    )}
                </div>
            </Dropdown.Toggle>

            <Dropdown.Menu className="message-menu shadow-lg">
                <div className="message-header">
                    <h6>메시지 목록</h6>
                    <Button
                        variant="link"
                        size="sm"
                        onClick={managerMessageDeleteAll}
                        title="모두 지우기"
                    >
                        <FaTrash />
                    </Button>
                </div>


                <div className="message-list custom-scrollbar">
                    {sortedManagerMessages.length > 0 ?
                        sortedManagerMessages.map((message) => (
                            <div
                                key={message.id}
                                className={`message-item ${message.read ? 'read' : 'unread'}`}
                                onClick={() => {
                                    managerMessageRead(message.id);
                                }}
                            >
                                <div className="message-icon">
                                    <FaEnvelope />
                                </div>
                                <div className="message-content-wrapper">
                                    <div className="message-title">{message.title}</div>
                                    <div className="message-body">{message.content}</div>
                                    <div className="message-time">
                                        {new Date(message.timestamp).toLocaleString()}
                                    </div>
                                </div>
                                <button
                                    className="delete-item-btn"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        managerMessageDelete(message.id);
                                    }}
                                >
                                    <FaTrashAlt />
                                </button>

                            </div>
                        ))
                        :
                        <div className="message-empty">
                            <FaEnvelope className="empty-icon" />
                            <p>메시지가 없습니다.</p>
                        </div>
                    }
                    {isAuthenticated && currentPage < totalPages - 1 && (
                        <div className="load-more-container text-center py-2">
                            <Button
                                variant="link"
                                size="sm"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    loadMoreManagerMessages();
                                }}
                                disabled={isLoadingMore}
                            >
                                {isLoadingMore ? '로딩 중...' : '더 보기'}
                            </Button>
                        </div>
                    )}
                </div>

            </Dropdown.Menu>
        </Dropdown>
    );
};

export default MessageCenter;