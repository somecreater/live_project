import { useNavigate } from "react-router-dom";
import UpdateForm from "../form/UpdateForm";
import { userStateStore } from "../../common/context/userStateStore";
import { Button } from "react-bootstrap";

function UpdateUserPage(){
  const navigate= useNavigate();

  return (
    <div className="container d-flex justify-content-center align-items-center mt-5">
      <UpdateForm/>
      <div>
        <Button>
          뒤로 가기
        </Button>
        
      </div>
    </div>
  );
}

export default UpdateUserPage;