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
      const response= await ApiService.user.update(updateUser);
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
      

    </Form>
  );
}

export default UpdateForm;