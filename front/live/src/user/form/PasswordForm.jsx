import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import ApiService from "../../common/api/ApiService";

function PasswordForm({props}){

  const [passwordResetRequest,setPasswordResetRequest]=useState({
    userId:'',
    org_pass:'',
    new_pass:''
  });
  const handleChange = (e) => {
    const { name, value } = e.target;
    setPasswordResetRequest((prev) => ({
      ...prev,
      [name]: value,
    }));
  };
  const handlePasswordChange= async (e)=>{
    e.preventDefault();
    const response= await ApiService.user.reset_password({
      userId: passwordResetRequest.userId,
      org_pass: passwordResetRequest.org_pass,
      new_pass: passwordResetRequest.new_pass
    });
    const data= response.data;
    try{
      if(data.result){
        alert(data.UserId + '비밀번호 변경 성공');
      }else{
        alert(data.UserId + '비밀번호 변경 실패');
      }
    }catch(error){
      alert("비밀번호 변경 실패");
      console.log(error);
    }
  }

  return (
    <Form className="p-4 border rounded shadow-sm" onSubmit={handlePasswordChange}>
      <Form.Floating  className="mb-3">
        <Form.Label  className="form-label">ID</Form.Label>
        <Form.Control
          type="text" 
          name="userId" 
          placeholder="ID" 
          value={passwordResetRequest.userId} 
          onChange={handleChange}
        />
      </Form.Floating>

      <Form.Floating  className="mb-3">
        <Form.Label  className="form-label">Orginal Password</Form.Label>
        <Form.Control 
          className="form-control" 
          type="password" 
          name="org_pass" 
          placeholder="orginal password" 
          value={passwordResetRequest.org_pass} 
          onChange={handleChange}
        />
      </Form.Floating>
                
      <Form.Floating  className="mb-3">
        <Form.Label  className="form-label">New Password</Form.Label>
        <Form.Control 
          className="form-control" 
          type="password" 
          name="new_pass" 
          placeholder="new password" 
          value={passwordResetRequest.new_pass} 
          onChange={handleChange}
        />
      </Form.Floating>
                
      <Button variant="primary" type="submit">비밀번호 변경</Button>
    </Form>
  );
}

export default PasswordForm;