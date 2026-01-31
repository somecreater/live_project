import { useEffect, useState } from "react";
import { userStateStore } from "../../common/context/userStateStore";
import { useNavigate } from "react-router-dom";
import ApiService from "../../common/api/ApiService";
import userManagerApi from "../hooks/useManagerApi";
import useManagerApi from "../hooks/useManagerApi";

/**
 * 관리자 페이지 컴포넌트(Tab으로 전환)
 * @param {Object} props - 컴포넌트 props
 */
function ManagerPage({ props }) {
  const { user } = userStateStore();
  const { getList, deleteResource, sendMessage, loading, error } = useManagerApi();
  const [activeTab, setActiveTab] = useState("USER");
  const [resourceType, setResourceType] = useState("USER");
  const [sendMessageModalOpen, setSendMessageModalOpen] = useState(false);
  const [resourcePage, setResourcePage] = useState({
    page: 1,
    size: 10,
    totalPage: 0,
    totalElements: 0,
    content: []
  });
  const [ListRequest, setListRequest] = useState({
    page: 1,
    size: 10,
    type: "all",
    keyword: "",
  });

  useEffect(() => {
    if (user.userType !== "MANAGER") {
      navigate("/");
    }
  }, [user.userType]);

  const navigate = useNavigate();

  const handleResourceTabChange = (tab) => {
    setActiveTab(tab);
    setResourceType(tab);

    setListRequest({
      page: 1,
      size: 10,
      type: "all",
      keyword: "",
    });
    handleSearchSubmit();
  };
  const handleSearchChange = (type, value) => {
    setListRequest({
      ...ListRequest,
      [type]: value,
    });
  };

  const handleSearchSubmit = async () => {
    const searchResult = getList(resourceType, ListRequest);
    if (searchResult) {
      setResourcePage({
        content: searchResult.content,
        page: searchResult.page,
        size: searchResult.size,
        totalPage: searchResult.totalPage,
        totalElements: searchResult.totalElements,
      });

      //resourceTable 업데이트
    }
  };
  const handleDelete = async (id) => {
    const deleteResult = deleteResource(resourceType, id);
    if (deleteResult) {
      handleSearchSubmit();
    }
  };
  //api 호출이 아닌 메시지 전송 모달 호출
  const handleSendMessage = async (loginId) => {
    setSendMessageModalOpen(true);

  };

  return (
    <div>

    </div>
  );
}

export default ManagerPage;