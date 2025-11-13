import { useState } from "react";
import { Form } from "react-bootstrap";
import { userStateStore } from "../../common/context/userStateStore";
import ApiService from "../../common/api/ApiService";

function LoginForm(){
  const [loginId,setLoginId] = useState("");
  const [pass,setPass] = useState("");
  const {getUserInfo, user, isAuthenticated} = userStateStore();

  const handleLogin = async (e)=>{
    e.preventDefault();
    try{
      const response= await ApiService.user.login({loginId,pass});
      const data=response.data;

      if(data.result){
        getUserInfo();
      }

    }catch(error){

    }
  };

  return (
    <Form>
      <Form.Group className="mb-3">
        <Form.Control type="text" placeholder="ID"/>
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Control type="password" placeholder="password"/>
      </Form.Group>

      <Button variant="primary" onClick={handleLogin}>Login</Button>

    </Form>
  );
}