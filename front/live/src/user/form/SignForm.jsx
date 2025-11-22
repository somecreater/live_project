import { useState } from "react";
import { Form, Button, InputGroup } from "react-bootstrap";
import ApiService from "../../common/api/ApiService";
import { useNavigate } from "react-router-dom";

function SignForm(){
  const navigate=useNavigate();
  const [mailSend,setMailSend]= useState(false);
  const [code,setCode]= useState("");
  const [user,setUser]=useState({
    loginId:"",
    password:"",
    phone:"",
    email:"",
    emailVerification:false,
    nickname:"",
    loginType:"NORMAL",
    userType:"NORMAL"
  });

  const handleChange = (e) => {
    const { name, value } = e.target;

    if(name === "email"){
      setUser((prev) => ({
        ...prev,
        email: value,
        emailVerification: false,
      }));
      setMailSend(false);
      setCode("");
      return;
    }

    setUser((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleEmailVerification = async (e) =>{
    try{
      const response= await ApiService.mail.send_verification({
        email: user.email
      });
      const data=response.data;

      if(data.result){
        alert(data.email+" 으로 메일을 전송하였으니 인증코드확인해보세요.");
        setMailSend(true);
      }else{
        alert(data.email+" 으로 이메일 전송에 실패했습니다."
        + "메일주소가 틀렸거나, 하루 제한횟수를 초과하였습니다.");
      }
    }catch(err){
      console.error(err);
    }
  };
  const handleEmailCheck = async (e) => {
    try{
      const response= await ApiService.mail.check_verification({
        email: user.email,
        token: code
      });
      const data= response.data;

      if(data.result){
        alert(data.email+"으로 인증되었습니다. 만약 이메일 변경시 재인증이 필요합니다.");
        setUser(prev => ({ ...prev, emailVerification: true }));
      }else{
        alert(data.email+"이 인증에 실패했습니다. 다시 코드를 확인해주세요.");
      }
    }catch(error){
      console.error(err);
    }
  }
  const handleSign = async (e) => {
    try{
      console.log(user);
      const response = await ApiService.user.register(user);
      const data=response.data;
      
      if(data.result){
        alert('안녕하세요. '+data.userID+' 님');
        navigate("/user/login");
      }else{
        alert('회원가입에 실패했습니다.'); 
      }
      
    }catch(error){
      alert(error);
    }
  };

  return(
    <Form className="p-4 border rounded shadow-sm" style={{ maxWidth: "400px", margin: "0 auto" }}>
      <Form.Floating  className="mb-3">
        <Form.Label className="form-label">ID</Form.Label>
        <Form.Control className="form-control" type="text" name="loginId" placeholder="ID" value={user.loginId} onChange={handleChange}/>
      </Form.Floating>

      <Form.Floating  className="mb-3">
        <Form.Label  className="form-label">Password</Form.Label>
        <Form.Control className="form-control" type="password" name="password" placeholder="password" value={user.password} onChange={handleChange}/>
      </Form.Floating>

      <Form.Floating  className="mb-3">
        <Form.Label className="form-label">휴대폰 번호</Form.Label>
        <Form.Control className="form-control" type="text" name="phone" placeholder="phone" value={user.phone} onChange={handleChange}/>
      </Form.Floating>

      <Form.Floating  className="mb-3">
        <Form.Label className="form-label">이메일</Form.Label>
        <Form.Control className="form-control" type="text" name="email" placeholder="email" value={user.email} onChange={handleChange}/>
        {user.emailVerification ?(
          <div>
            <p className="text-muted mt-3 text-center">✓ 이메일 인증 완료</p>
          </div>
        ):(
          <div>
            {mailSend?(
              (
                <div>
                  <InputGroup className="mb-3">
                    <InputGroup.Text>CODE</InputGroup.Text>
                    <Form.Control className="form-control" 
                      type="text" 
                      name="code"
                      placeholder="EMAIL CODE"
                      value={code}
                      onChange={(e)=>setCode(e.target.value)}
                    />
                  </InputGroup>
                  <Button 
                    type="button"
                    className="btn btn-primary w-100 mt-2"
                    onClick={handleEmailCheck}
                  >
                    인증코드 확인
                  </Button>
                </div>
              )
            ):(
              <div>
                <Button 
                  type="button"
                  className="btn btn-primary w-100 mt-2"
                  onClick={handleEmailVerification}
                >
                  인증코드 전송
                </Button>
              </div>
            )}
            <p className="text-muted mt-3 text-center">✗ 이메일 미인증</p>
          </div>
        )}
        
      </Form.Floating>
      
      <Form.Floating  className="mb-3">
        <Form.Label className="form-label">NickName</Form.Label>
        <Form.Control className="form-control" type="text" name="nickname" placeholder="nickname" value={user.nickname} onChange={handleChange}/>
      </Form.Floating>

      <Form.Group  className="mb-3">
        <Form.Label className="form-label">권한</Form.Label>
        
        <div className="form-check">
        <Form.Check
          className="form-check-input"
          type="radio"
          label="NORMAL"
          name="userType"
          value="NORMAL"
          onChange={handleChange}
        />
        </div>
        
        <div className="form-check">
        <Form.Check
          className="form-check-input"
          type="radio"
          label="STREAMER"
          name="userType"
          value="STREAMER"
          onChange={handleChange}
        />
        </div>
        
        <div className="form-check">
        <Form.Check
          className="form-check-input"
          type="radio"
          label="MANAGER"
          name="userType"
          value="MANAGER"
          onChange={handleChange}
        />
        </div>
      </Form.Group>
      
      <Button 
        className="btn btn-primary w-100 mt-2"
        onClick={handleSign}>
        Sign
      </Button>
    </Form>
  );
}

export default SignForm;