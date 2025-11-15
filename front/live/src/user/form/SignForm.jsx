import { useState } from "react";
import { Form, Button } from "react-bootstrap";
import ApiService from "../../common/api/ApiService";
import { useNavigate } from "react-router-dom";

function SignForm(){
  const navigate=useNavigate();
  const [user,setUser]=useState({
    loginId:"",
    password:"",
    phone:"",
    nickname:"",
    loginType:"NORMAL",
    userType:"NORMAL"
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

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
        <Form.Label className="form-label">PHONE</Form.Label>
        <Form.Control className="form-control" type="text" name="phone" placeholder="phone" value={user.phone} onChange={handleChange}/>
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