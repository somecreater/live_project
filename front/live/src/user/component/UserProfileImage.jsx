import { useEffect, useState } from "react";
import { userStateStore } from "../../common/context/userStateStore";
import { Image } from "react-bootstrap";

function UserProfileImage({props}){
  const {user, defaultProfileImageUrl, profileImageUrl, getUserProfile}=userStateStore();


  useEffect(()=>{
    if (!profileImageUrl && user?.loginId) {
      getUserProfile(user.loginId);
    }
  });
  
  const imgSrc= profileImageUrl||defaultProfileImageUrl;

  return(
    <Image
      width={100}
      height={100}
      src={imgSrc}  
      style={{ objectFit: 'cover', borderRadius: '50%', backgroundColor:'whitesmoke'}}
      rounded
    />
  );
}

export default UserProfileImage;