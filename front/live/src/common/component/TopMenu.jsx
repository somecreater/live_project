import { Container, Dropdown, Image, Nav, Navbar, NavItem, NavLink } from "react-bootstrap";
import { Link } from "react-router-dom";
import { userStateStore } from "../context/userStateStore";
import { useEffect } from "react";
import UserDropDown from "./UserDropDown";

const TopItem = [
  { title: "홈", link: "/" },
  { title: "실시간 방송", link: "/" },
  { title: "동영상", link: "/" },
  { title: "인기 동영상", link: "/" },
  { title: "인기 실시간 방송", link: "/" },
  { title: "채널", link: "/channel" }
];
function TopMenu({ props }) {
  const logoUrl = "/public/image/Logo.png";
  const { user, getUserProfile, getUserProfileCache, profileImageUrl } = userStateStore();
  useEffect(() => {
    if (user?.loginId) {
      getUserProfile(user?.loginId);
    } else {
      getUserProfileCache();
    }
  }, [profileImageUrl, user?.loginId]);
  return (
    <Navbar bg="danger" variant="dark" fixed="top">
      <Container fluid>
        <Navbar.Brand as={Link} to="/" className="d-flex align-items-center">
          <Image
            src={logoUrl}
            width={70}
            height={70}
            style={{ objectFit: 'cover', borderRadius: '50%', backgroundColor: 'whitesmoke', marginRight: '8px' }}
            rounded
          />
          <span>YourLive</span>
        </Navbar.Brand>
        <Nav
          className="d-flex flex-row"
          style={{ gap: '1rem' }}
        >
          {TopItem.map(item => (
            <Nav.Item key={item.title}>
              <Nav.Link as={Link} to={item.link}>{item.title}</Nav.Link>
            </Nav.Item>
          ))}
        </Nav>
        <Nav className="ms-auto">
          <UserDropDown />
        </Nav>
      </Container>

    </Navbar>
  );
}

export default TopMenu;