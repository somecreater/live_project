import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import ApiService from "../../common/api/ApiService";

function DeleteForm({props}){
  const navigate=useNavigate();
  const [deleteRequest,setDeleteRequest]=useState({
    loginId: '',
    pass:''
  });
  const handleChange = (e) => {
    const { name, value } = e.target;
    setDeleteRequest((prev) => ({
      ...prev,
      [name]: value,
    }));
  };
  
  const handleDelete= async (e)=>{
    e.preventDefault();
    try{
      const response= await ApiService.user.delete({
        loginId: deleteRequest.loginId,
        pass: deleteRequest.pass
      });
      const data= response.data;
      if(data.result){
        alert(data.UserId+" 회원탈퇴가 정상적으로 처리되었습니다.");
      }else{
        alert(data.UserId+" 회원탈퇴에 실패했습니다.");
      }
    }catch(error){
      console.log(error);
    }

  };

  return (
    <Form className="p-4 border rounded shadow-sm" onSubmit={handleDelete}>
      <Form.Floating className="mb-3">
        <Form.Label  className="form-label">ID</Form.Label>
        <Form.Control 
          className="form-control" 
          type="text" 
          name="loginId" 
          placeholder="login ID" 
          value={deleteRequest.loginId} 
          onChange={handleChange}
        />
      </Form.Floating>

      <Form.Floating className="mb-3">
        <Form.Label  className="form-label">passowrd</Form.Label>
        <Form.Control 
          className="form-control" 
          type="password" 
          name="pass" 
          placeholder="password" 
          value={deleteRequest.pass} 
          onChange={handleChange}
        />
      </Form.Floating>

      <Button variant="primary" type="submit">회원 탈퇴</Button>
    </Form>
  );
}

export default DeleteForm;