import { useState } from "react";
import { userStateStore } from "../../common/context/userStateStore";

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

  };
  const handeUpdate= (e)=>{

  }
  
  return (
    <Form className="p-4 border rounded shadow-sm" style={{ maxWidth: "400px", margin: "0 auto" }}>


    </Form>
  );
}

export default UpdateForm;