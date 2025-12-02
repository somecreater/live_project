import { useEffect, useState } from "react";
import { Button, Nav } from "react-bootstrap";

const SideItem=[
  {title:"구독", link:"/"}, 
  {title:"기록", link:"/"}, 
  {title:"추천한 동영상", link:"/"},
  {title:"비추천한 동영상", link:"/"},
  {title:"저장한 영상", link:"/"}
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
      <Nav defaultActiveKey="/home" className="flex-column pt-3">
        {loginId?
        (SideItem.map(item=>(
          <Nav.Item key={item.title}>
            <Nav.Link href={item.link}>
              { !collapsed ? item.title :  item.title.charAt(0) }
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