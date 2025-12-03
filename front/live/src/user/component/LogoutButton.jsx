import { Button } from "react-bootstrap";
import ApiService from "../../common/api/ApiService";
import { useNavigate } from "react-router-dom";
import { userStateStore } from "../../common/context/userStateStore";

function LogoutButton(){
  const navigate= useNavigate();
  const {clearProfileImage}= userStateStore();
  const logout= async ()=>{
    try{
      await ApiService.user.logout();
      localStorage.removeItem("loginId");
      localStorage.removeItem("profileImageUrl");
      clearProfileImage();
      navigate('/user/login');
    }catch(error){
      alert("로그아웃 실패!!");
      console.error(error);
    }
  };

  return (
    <Button 
      type="button" 
      variant="danger"
      onClick={logout} 
    >
    로그아웃
    </Button>
  );  
}

export default LogoutButton;