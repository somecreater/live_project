import { useRef, useState } from "react";
import { userStateStore } from "../../common/context/userStateStore";
import ApiService from "../../common/api/ApiService";
import { Button, Form, Image } from "react-bootstrap";
import UserProfileImage from "../component/UserProfileImage";

function UserProfileImageForm(){
  const {user, getUserProfile}= userStateStore();
  const [preViewUrl, setPreViewUrl]= useState(null);
  const [file,setFile]= useState(null);
  const fileInputRef= useRef();
  
  const handleFilechange= (e)=>{
    const selected = e.target.files?.[0];
    if(!selected){
      setPreViewUrl('');
      setFile(null);
      return;
    }
    console.log(selected);
    setFile(selected);

    const url=URL.createObjectURL(selected);
    setPreViewUrl(url);
  };
  const handleFileCancle= ()=>{
    setFile(null);
    setPreViewUrl('');
  };
  const fileUpload= async (e) => {
    e.preventDefault();
    if(file == null){
      alert("먼저 파일을 선택하세요.");
      return;
    }
    try{
      const formData= new FormData();
      formData.append("file", file);
      const response= await ApiService.profile_image.upload(formData);
      const data= response.data;
      const message= `${user.loginId} 님이 프로필 이미지를 수정하셨습니다. (${data.image.size} byte)`;
      alert(message);
      getUserProfile(user.loginId);
    }catch(error){
        console.error(error);
    }
  }

  return (
    <Form onSubmit={fileUpload}>
      <Form.Group>
        <Form.Label>기존 프로필 이미지</Form.Label><br/>
        <UserProfileImage/>
      </Form.Group>

      {preViewUrl?(
        <Image
          width={100}
          height={100}
          src={preViewUrl}
          style={{ objectFit: 'cover', borderRadius: '50%', backgroundColor:'whitesmoke'}}
          rounded
        />
      ):(
        <div style={{
          width: 160, height: 160, 
          border: "1px dashed #ccc", 
          borderRadius: "50%", 
          display: "flex", 
          alignItems: "center", 
          justifyContent: "center" }}>
          <span>프로필 이미지</span>
        </div>
      )}
       <Form.Control type="file" ref={fileInputRef} onChange={handleFilechange}/>

       {file !=null?
        <Button type="submit" variant="primary">프로필 이미지 등록(수정)하기</Button>:
        <Button type="button" variant="danger" onClick={handleFileCancle}>프로필 이미지 취소</Button>
       }
     </Form>
  );
}

export default UserProfileImageForm;