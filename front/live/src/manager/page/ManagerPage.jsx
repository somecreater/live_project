import { useEffect, useState } from "react";
import { userStateStore } from "../../common/context/userStateStore";
import { useNavigate } from "react-router-dom";
import ApiService from "../../common/api/ApiService";

// 관리자 페이지
/* 
사용할 리소스: UserList, ChannelList, VideoList, PostList
*/
function ManagerPage({ props }) {
  const { user } = userStateStore();
  const [resourcePage, setResourcePage] = useState({
    resourceType: "USER",
    page: 1,
    size: 10,
    totalPage: 0,
    totalElements: 0,
    content: []
  });
  const [ListRequest, setListRequest] = useState({
    resourceType: "USER",
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


  return (
    <div>
    </div>
  );
}

export default ManagerPage;