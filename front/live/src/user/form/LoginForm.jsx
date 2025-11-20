import { useState } from "react";
import { Form, Button } from "react-bootstrap";
import { userStateStore } from "../../common/context/userStateStore";
import ApiService from "../../common/api/ApiService";
import { useNavigate } from "react-router-dom";
import { API_END_POINT } from "../../common/api/Api";

function LoginForm(){
  const [loginId,setLoginId] = useState("");
  const [pass,setPass] = useState("");
  const {getUserInfo} = userStateStore();
  const navigate=useNavigate();

  const handleSign = ()=>{
    navigate("/user/sign");
  };
  const handleGoogleLogin = ()=>{
    window.location.href= API_END_POINT.user.googleLogin;
  };
  const handleKakaoLogin = ()=>{
    window.location.href= API_END_POINT.user.kakaoLogin;
  };
  const handleLogin = async (e)=>{
    e.preventDefault();
    try{
      const response= await ApiService.user.login({loginId,pass});
      const data=response.data;

      if(data.result){
        getUserInfo();
        navigate("/user/mypage");
      }

    }catch(error){
      alert(error);
    }
  };

  return (
    <Form className="p-4 border rounded shadow-sm" style={{ maxWidth: "400px", margin: "0 auto" }} onSubmit={handleLogin}>
      <Form.Group className="mb-3">
        <Form.Control type="text" placeholder="ID" value={loginId} onChange={(e) => setLoginId(e.target.value)}/>
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Control type="password" placeholder="password" value={pass} onChange={(e) => setPass(e.target.value)}/>
      </Form.Group>

      <Button variant="info" onClick={handleSign}>Sign</Button>
      <Button type="submit" variant="primary">Login</Button>
      <Button type="button" variant="secondary" onClick={handleGoogleLogin}>Google Login</Button>
      <Button type="button" variant="secondary" onClick={handleKakaoLogin}>Kakao Login</Button>
    </Form>
  );
}

export default LoginForm;