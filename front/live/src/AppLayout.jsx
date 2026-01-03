import { useState } from "react";
import Footer from "./common/component/Footer";
import SideMenu from "./common/component/SideMenu";
import TopMenu from "./common/component/TopMenu";
import AlertSystem from "./common/component/AlertSystem";

function AppLayout({ children }) {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  return (
    <>
      <div className="app-container">
        <AlertSystem />
        <TopMenu />
        <div className="content-wrapper">
          <aside className={`sidebar ${sidebarCollapsed ? "collapsed" : ""}`}>
            <SideMenu collapsed={sidebarCollapsed} toggle={() => setSidebarCollapsed(!sidebarCollapsed)} />
          </aside>
          <main className={`main-area ${sidebarCollapsed ? "collapsed" : ""}`}>
            {children}
          </main>
        </div>
        <Footer />
      </div>
    </>
  );
}

export default AppLayout;