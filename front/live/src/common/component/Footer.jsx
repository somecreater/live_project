import { Container, Navbar } from "react-bootstrap";

function Footer({props}){

  return (
    <Navbar bg="danger" variant="dark" fixed="bottom">
      <Container fluid>
      <div className="container text-center">
        <span>© 2025 YourLive Site. All rights reserved.</span>
      </div>
      </Container>
    </Navbar>
  );  
}

export default Footer;