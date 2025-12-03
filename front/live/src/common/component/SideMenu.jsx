import { useEffect, useState } from "react";
import { Button, Nav } from "react-bootstrap";
import { FaHome, FaHistory, FaThumbsUp, FaThumbsDown, FaBookmark } from "react-icons/fa";

const SideItem=[
  {title:"구독", link:"/", icon:<FaHome/>}, 
  {title:"기록", link:"/", icon:<FaHistory/>}, 
  {title:"추천한 동영상", link:"/", icon:<FaThumbsUp/>},
  {title:"비추천한 동영상", link:"/", icon:<FaThumbsDown/>},
  {title:"저장한 영상", link:"/", icon:<FaBookmark/>}
];

function SideMenu({ collapsed, toggle }){
  const [loginId, setLoginId] = useState(null);
  
  useEffect(() => {
    const id = localStorage.getItem("loginId");
    if (loginId !== id) {
      setLoginId(id);
    }
  });
  
  return (
      <div className="sidebar-inner">
        <Button className="toggle-btn" onClick={toggle}>
          {collapsed ? "▶" : "◀"}
        </Button>
      <Nav defaultActiveKey="/home" className="flex-column nav-menu">
        {loginId?
        (SideItem.map(item=>(
          <Nav.Item key={item.title}>
            <Nav.Link href={item.link} className="nav-link-custom">
              <span className="icon">{item.icon}</span>
              { !collapsed && <span className="link-text">{item.title}</span>}
            </Nav.Link>
          </Nav.Item>
        ))
        ):(
        <Nav.Item>
          <Nav.Link href={"/user/login"}>
            로그인 하기
          </Nav.Link>
        </Nav.Item>
        )
        }
      </Nav>
      </div>
  );
}

export default SideMenu;