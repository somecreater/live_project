import { Button } from "react-bootstrap";
import ApiService from "../../common/api/ApiService";
import { useNavigate } from "react-router-dom";

function LogoutButton(){
  const navigate= useNavigate();
  const logout= async ()=>{
    try{
      await ApiService.user.logout();
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