import { useState } from "react";
import { userStateStore } from "../../common/context/userStateStore";
import { Form } from "react-bootstrap";
import ApiService from "../../common/api/ApiService";

function UpdateForm(){
  const {getUserInfo, user, isAuthenticated}=userStateStore();
  const {updateUser,setUpdateUser}=useState({
    loginId: user.loginId,
    email: user.email,
    password: '',
    phone: user.phone,
    nickname: user.nickname,
    loginType: user.loginType,
    userType: user.userType,
    createdAt: user.createdAt,
    updateAt: user.updateAt
  });

  const handleChange= (e)=>{
    const { name, value } = e.target;
    setUpdateUser((prev) => ({
      ...prev,
      [name]: value,
    }));
  };
  const handeUpdate= async (e)=>{
    try{
      const response= await ApiService.user.update({
        loginId: user.loginId,
        email: user.email,
        password: updateUser.password,
        phone: updateUser.phone,
        nickname: updateUser.nickname,
        loginType: user.loginType,
        userType: updateUser.userType,
        createdAt: user.createdAt,
        updateAt: user.updateAt
      });
      const data= response.data;
      const user= data.newUser;
      if(data.result){
         alert(user.loginId+"정상적으로 회원 정보가 변경되었습니다.");
      }
    }catch(error){
      console.log(error);
    }
  };
  
  return (
    <Form className="p-4 border rounded shadow-sm" style={{ maxWidth: "400px", margin: "0 auto" }}>
      
      <Form.Floating  className="mb-3">
        <Form.Label className="form-label">email</Form.Label>
        {user.loginType == 'NORMAL' ?
          <Form.Control className="form-control" type="text" name="email" placeholder="email" value={updateUser.email} onChange={handleChange}/>
        : <Form.Text className="form-control">{user.email}</Form.Text>
        }
      </Form.Floating>

      <Form.Floating  className="mb-3">
        <Form.Label className="form-label">phone</Form.Label>
        <Form.Control className="form-control" type="text" name="phone" placeholder="phone" value={updateUser.phone} onChange={handleChange}/>
      </Form.Floating>

      <Form.Floating  className="mb-3">
        <Form.Label className="form-label">NickName</Form.Label>
        <Form.Control className="form-control" type="text" name="nickname" placeholder="nickname" value={updateUser.nickname} onChange={handleChange}/>
      </Form.Floating>

    </Form>
  );
}

export default UpdateForm;