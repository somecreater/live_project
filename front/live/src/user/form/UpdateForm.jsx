import { useEffect, useState } from "react";
import { userStateStore } from "../../common/context/userStateStore";
import { Form,Button } from "react-bootstrap";
import ApiService from "../../common/api/ApiService";
import { useNavigate } from "react-router-dom";

function UpdateForm(){
  const navigate= useNavigate();
  const {getUserInfo, user, isAuthenticated}=userStateStore();
  const [updateUser,setUpdateUser]=useState({
    loginId: "",
    email: "",
    password: "",
    phone: "",
    nickname: "",
    loginType: "",
    userType: "",
    createdAt: "",
    updateAt: ""
  });

  useEffect(() => {
    if (user) {
      setUpdateUser({
        loginId: user.loginId,
        email: user.email,
        password: "",
        phone: user.phone,
        nickname: user.nickname,
        loginType: user.loginType,
        userType: user.userType,
        createdAt: user.createdAt,
        updatedAt: user.updatedAt
      });
    }
  }, [user]);

  const handleChange= (e)=>{
    const { name, value } = e.target;
    setUpdateUser((prev) => ({
      ...prev,
      [name]: value,
    }));
  };
  const handleUpdate= async (e)=>{
    e.preventDefault();
    try{
      const response= await ApiService.user.update({
        loginId: user.loginId,
        email: updateUser.email,
        password: updateUser.password,
        phone: updateUser.phone,
        nickname: updateUser.nickname,
        loginType: user.loginType,
        userType: updateUser.userType,
        createdAt: user.createdAt,
        updateAt: user.updatedAt
      });

      const data= response.data;
      const newUser = data.newUser;
      if(data.result){
         alert(newUser.loginId+"님, 정상적으로 회원 정보가 변경되었습니다.");
         getUserInfo();
         navigate('/user/mypage');
      }
    }catch(error){
      console.log(error);
    }
  };

  if (!user || !isAuthenticated) return <p>Loading...</p>;

  return (
    <Form 
      onSubmit={handleUpdate}
      className="p-4 border rounded shadow-sm" 
      style={{ maxWidth: "400px", margin: "0 auto" }}
    >
      <Form.Group  className="mb-3">
        <Form.Label className="form-label">email</Form.Label>
        {user.loginType == 'NORMAL' ?
        (
          <Form.Control
            type="text"
            name="email"
            placeholder="email"
            value={updateUser.email}
            onChange={handleChange}
          />
        ):(
          <Form.Control plaintext readOnly defaultValue={user.email} />
        )}
      </Form.Group>

      <Form.Group  className="mb-3">
        <Form.Label className="form-label">phone</Form.Label>
        <Form.Control 
          className="form-control" 
          type="text" 
          name="phone" 
          placeholder="phone" 
          value={updateUser.phone} 
          onChange={handleChange}
        />
      </Form.Group>

      <Form.Group  className="mb-3">
        <Form.Label className="form-label">NickName</Form.Label>
        <Form.Control 
          className="form-control" 
          type="text" 
          name="nickname" 
          placeholder="nickname" 
          value={updateUser.nickname} 
          onChange={handleChange}
        />
      </Form.Group>

      <Button variant="primary" type="submit">회원정보 수정</Button>

    </Form>
  );
}

export default UpdateForm;