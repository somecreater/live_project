import { useState } from "react";
import { Button, Card, Col, Container, Form, InputGroup, Row, Tab, Tabs } from "react-bootstrap";
import ApiService from "../../common/api/ApiService";

function SearchForm(){
  
  const [IdEmail, setIdEmail]= useState('');
  const [PassEmail, setPassEmail]= useState('');
  const [LoginId, setLoginId]= useState('');  
  const [token, setToken]= useState('');
  const [sendMail, setSendMail]= useState(false);
  const [sendPassMail, setSendPassMail]= useState(false);
  const [activeTab, setActiveTab]= useState('findId');

  const handleTabSelect= (k)=>{
    if (k == null) return;
    console.log('selected tab:', k);
    setActiveTab(k);
    setToken('');
    setIdEmail('');
    setPassEmail('');
    setLoginId('');
    setSendMail(false);
    setSendPassMail(false);
  }

  const SendMailForId = async (e) => {
    try{
      e.preventDefault();
      setSendMail(true);
      const response= await ApiService.mail.send_mail_login_id({
        email: IdEmail  
      });
      const data= response.data;
      alert(`${data.email}로 메일전송을 처리했습니다.`);
    }catch(error){
      console.error(error);
    }
  };
  const SendMailForPass = async (e) => {
    try{
      e.preventDefault();
      setSendPassMail(true);
      const response= await ApiService.mail.send_mail_password({
        loginId:LoginId,
        email:PassEmail  
      });
      const data= response.data;
      alert(`${data.email}로 메일전송을 처리했습니다.`);
    }catch(error){
      console.error(error);  
    }
  };
  const handleSearchId = async (e) => {
    try{
      e.preventDefault();
      const response= await ApiService.mail.search_login_id({
        email:IdEmail,
        token:token  
      });
      const data= response.data;
      alert(`해당 이메일로 가입된 아이디 입니다 \n\n\n ${data.login_id}`)
      alert(`가입하신 아이디를 모두 알고 싶다면 하단의 전화번호로 문의 주세요.`);
      setSendMail(false);
      setIdEmail("");
    }catch(error){
      console.error(error);  
      setSendMail(false);
      setIdEmail("");
    }
  };
  const handleSearchPass = async (e) => {
    try{
      e.preventDefault();
      alert("메일로 새로운 비밀번호 전송하니, 약 1분 정도 기다려주세요.");
      const response= await ApiService.mail.search_password({
        email:PassEmail,
        token:token  
      });
      const data= response.data;
      alert(data.message);
      setPassEmail("");
      setLoginId("");
      setSendPassMail(false);
    }catch(error){
      console.error(error);
      setPassEmail("");
      setLoginId("");
      setSendPassMail(false);
    }
  };

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col md={16} lg={16}>
          <Card className="shadow-sm">
            <Card.Body>
              <Tabs 
                id="search-form-tabs"
                defaultActiveKey="findId"
                activeKey={activeTab}
                onSelect={handleTabSelect}
                className="mb-3"
                justify
              >
                <Tab eventKey="findId" title="아이디 찾기">
                  <Form>
                    <Form.Group className="mb-3">
                      <Form.Label>등록된 이메일 주소 </Form.Label>
                      <Form.Control
                        type="email"
                        placeholder="example@domain.com"
                        value={IdEmail}
                        onChange={e => setIdEmail(e.target.value)}
                        required
                      />
                      <Form.Text className="text-muted">
                        가입하신 이메일 주소를 입력해주세요.
                      </Form.Text>
                    </Form.Group>
                    <Button
                      type="button"
                      variant="primary"
                      onClick={SendMailForId}
                    >
                      이메일 발송
                    </Button>
                    {sendMail &&(
                      <>
                        <InputGroup className="mb-3 mt-3">
                          <InputGroup.Text>CODE</InputGroup.Text>
                          <Form.Control
                            className="form-control" 
                            type="text" 
                            placeholder="EMAIL CODE(약 1분정도 대기)"
                            value={token}
                            onChange={(e)=>setToken(e.target.value)}
                          />
                        </InputGroup>
                        <Button 
                          type="button" 
                          variant="primary" 
                          className="w-100"
                          onClick={handleSearchId}
                        >
                          인증코드 확인
                        </Button>
                      </>
                    )}   
                  </Form>
                </Tab>
                <Tab eventKey="findPass" title="비밀번호 찾기">
                  <Form>
                    <Form.Group className="mb-3">
                      <Form.Label>등록된 이메일 주소 </Form.Label>
                      <Form.Control
                        type="email"
                        placeholder="example@domain.com"
                        value={PassEmail}
                        onChange={e =>setPassEmail(e.target.value)}
                        required
                      />
                      <Form.Text className="text-muted">
                        가입하신 이메일 주소를 입력해주세요.
                      </Form.Text>
                    </Form.Group>

                    <Form.Group>
                      <Form.Label>등록된 아이디</Form.Label>
                      <Form.Control
                        type="text"
                        placeholder="LOGIN ID"
                        value={LoginId}
                        onChange={e => setLoginId(e.target.value)}
                        required
                      />
                      <Form.Text className="text-muted">
                        가입하신 아이디를 입력해주세요.
                      </Form.Text>
                    </Form.Group>
                    <Button
                      type="button"
                      variant="primary"
                      onClick={SendMailForPass}
                    >
                      이메일 발송
                    </Button>
                    {sendPassMail &&( 
                      <>
                        <InputGroup className="mb-3 mt-3">
                          <InputGroup.Text>CODE</InputGroup.Text>
                          <Form.Control
                            className="form-control" 
                            type="text" 
                            placeholder="EMAIL CODE(약 1분정도 대기)"
                            value={token}
                            onChange={(e)=>setToken(e.target.value)}
                          />
                        </InputGroup>
                        <Button 
                          type="button"
                          variant="primary" 
                          className="w-100"
                          onClick={handleSearchPass}
                        >
                          인증코드 확인
                        </Button>
                      </>      
                    )}
                  </Form>
                </Tab>
              </Tabs>  
            </Card.Body>
          </Card> 
        </Col>
      </Row>  
    </Container>
  );
}

export default SearchForm;