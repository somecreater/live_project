import { useEffect, useState } from 'react';
import { userStateStore } from '../../common/context/userStateStore';
import User from '../component/User';
import { useNavigate } from 'react-router-dom';
import { Button, Modal } from 'react-bootstrap';
import PasswordForm from '../form/PasswordForm';
import CustomModal from '../../common/component/CustomModal';
import DeleteForm from '../form/DeleteForm';
import LogoutButton from '../component/LogoutButton';

function MyPage(){
  
  const {getUserInfo, user, isAuthenticated}= userStateStore();
  const [passwordModal,setPasswordModal]= useState(false);
  const [deleteModal,setDeleteModal]= useState(false);
  const navigate = useNavigate();

  useEffect(()=>{ getUserInfo(); },[]);
  const handlePasswordModal =() => setPasswordModal(true);
  const closePasswordModal =() => setPasswordModal(false);
  const handleDeleteModal =() => setDeleteModal(true);
  const closeDeleteModal =() => setDeleteModal(false);

  const passModalFooter=
  <Button 
    className="btn_close" 
    variant="secondary" 
    onClick={closePasswordModal}
  >
      닫기
  </Button>;
  const deleteModalFooter=
  <Button 
    className='btn_close'
    variant='secondary'
    onClick={closeDeleteModal}
  >
    닫기
  </Button>;

  return (
    <div className="container d-flex justify-content-center align-items-center mt-5">
        <User user={user} isAuthenticated={isAuthenticated}/>
        <div>
          <LogoutButton/>
          <Button onClick={handleDeleteModal}>회원 탈퇴</Button>
          <Button onClick={()=>navigate('/user/update')}>회원정보 수정</Button>
          <Button onClick={handlePasswordModal}>비밀번호 변경</Button>
          <CustomModal
            title={'회원 탈퇴'}
            modalState={deleteModal}
            close={closeDeleteModal}
            component={<DeleteForm/>}
            footer={deleteModalFooter}
          />
          <CustomModal 
            title={'비밀번호 변경'} 
            modalState={passwordModal}
            close={closePasswordModal}
            component={<PasswordForm/>}
            footer={passModalFooter}
          />
        </div>
    </div>
  );
}

export default MyPage;