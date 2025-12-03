import { Container } from "react-bootstrap";
import TopMenu from "../component/TopMenu";
import SideMenu from "../component/SideMenu";
import { userStateStore } from "../context/userStateStore";
import Footer from "../component/Footer";

function HomePage({props}){

  const {getUserInfo, user, isAuthenticated}= userStateStore();

  return (
    <div>
      <Container fluid className="p-0">
        
      </Container>
      <Container fluid className="p-0">
        
      </Container>
    </div>
  );
}

export default HomePage;