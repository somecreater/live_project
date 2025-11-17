import { useEffect, useState } from 'react';
import { userStateStore } from '../../common/context/userStateStore';
import User from '../component/User';
import { useNavigate } from 'react-router-dom';
import { Button, Modal, Form } from 'react-bootstrap';
import ApiService from '../../common/api/ApiService';

function MyPage(){
  
  const {getUserInfo, user, isAuthenticated}= userStateStore();
  const [passwordModal,setPasswordModal]= useState(false);
  const [passwordResetRequest,setPasswordResetRequest]=useState({
    userId:'',
    org_pass:'',
    new_pass:''
  });

  const navigate = useNavigate();
  useEffect(()=>{ getUserInfo(); },[]);
  const handlePasswordModal =() => setPasswordModal(true);
  const closePasswordModal =() => setPasswordModal(false);
  const handleChange = (e) => {
    const { name, value } = e.target;
    setPasswordResetRequest((prev) => ({
      ...prev,
      [name]: value,
    }));
  };
  const handlePasswordChange= async (e)=>{
    e.preventDefault();
    const response= await ApiService.user.reset_password({
      userId: passwordResetRequest.userId,
      org_pass: passwordResetRequest.org_pass,
      new_pass: passwordResetRequest.new_pass
    });
    const data= response.data;
    try{
      if(data.result){
        alert(data.UserId + '비밀번호 변경 성공');
      }else{
        alert(data.UserId + '비밀번호 변경 실패');
      }
    }catch(error){
      alert("비밀번호 변경 실패");
      console.log(error);
    }
      
  }

  return (
    <div className="container d-flex justify-content-center align-items-center mt-5">
        <User user={user} isAuthenticated={isAuthenticated}/>
        <div>
          <Button onClick={()=>navigate('/user/update')}>회원정보 수정</Button>
          <Button onClick={handlePasswordModal}>비밀번호 변경</Button>
          <Modal show={passwordModal} onHide={closePasswordModal}>
            <Modal.Header>
              <Modal.Title>비밀번호 변경</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <Form className="p-4 border rounded shadow-sm" onSubmit={handlePasswordChange}>
                
                <Form.Floating  className="mb-3">
                  <Form.Label  className="form-label">ID</Form.Label>
                  <Form.Control
                    type="text" 
                    name="userId" 
                    placeholder="ID" 
                    value={passwordResetRequest.userId} 
                    onChange={handleChange}
                  />
                </Form.Floating>

                <Form.Floating  className="mb-3">
                  <Form.Label  className="form-label">Orginal Password</Form.Label>
                  <Form.Control 
                    className="form-control" 
                    type="password" 
                    name="org_pass" 
                    placeholder="orginal password" 
                    value={passwordResetRequest.org_pass} 
                    onChange={handleChange}
                  />
                </Form.Floating>
                
                <Form.Floating  className="mb-3">
                  <Form.Label  className="form-label">New Password</Form.Label>
                  <Form.Control 
                    className="form-control" 
                    type="password" 
                    name="new_pass" 
                    placeholder="new password" 
                    value={passwordResetRequest.new_pass} 
                    onChange={handleChange}
                  />
                </Form.Floating>
                
                <Button variant="primary" type="submit">비밀번호 변경</Button>
              </Form>
            </Modal.Body>
            <Modal.Footer>
              <Button className="btn_close" variant="secondary" onClick={closePasswordModal}>
                닫기
              </Button>
            </Modal.Footer>
          </Modal>
        </div>
    </div>
  );
}

export default MyPage;