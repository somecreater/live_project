import { Dropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import LogoutButton from "../../user/component/LogoutButton";
import UserProfileImage from "../../user/component/UserProfileImage";

function UserDropDown() {
  const loginId = localStorage.getItem("loginId");

  return (
    <Dropdown align={"end"}>
      <Dropdown.Toggle
        as={"div"}
        id="user-menu"
        style={{ cursor: "pointer" }}
      >
        <UserProfileImage />
      </Dropdown.Toggle>
      {loginId ?
        <Dropdown.Menu>
          <Dropdown.Item as={Link} to="/user/mypage">마이페이지</Dropdown.Item>
          <Dropdown.Item as={Link} to="/channel/my_channel">나의 채널</Dropdown.Item>
          <Dropdown.Divider />
          <Dropdown.Item><LogoutButton /></Dropdown.Item>
        </Dropdown.Menu>
        :
        <Dropdown.Menu>
          <Dropdown.Item as={Link} to="/user/login">로그인 하기</Dropdown.Item>
        </Dropdown.Menu>
      }
    </Dropdown>
  );
}

export default UserDropDown;