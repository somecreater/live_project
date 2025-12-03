import { useEffect, useState } from 'react';
import { userStateStore } from '../../common/context/userStateStore';
import User from '../component/User';
import { useNavigate } from 'react-router-dom';
import { Button, Card, Col, Container, Modal, Row } from 'react-bootstrap';
import PasswordForm from '../form/PasswordForm';
import CustomModal from '../../common/component/CustomModal';
import DeleteForm from '../form/DeleteForm';
import LogoutButton from '../component/LogoutButton';
import UserProfileImage from '../component/UserProfileImage';
import UserProfileImageForm from '../form/UserProfileImageForm';

function MyPage(){
  
  const {getUserInfo, user, isAuthenticated}= userStateStore();
  const [passwordModal,setPasswordModal]= useState(false);
  const [deleteModal,setDeleteModal]= useState(false);
  const [profileImgModal,setProfileImgModal]= useState(false);
  const navigate = useNavigate();

  useEffect(()=>{ getUserInfo(); },[]);
  const handlePasswordModal =() => setPasswordModal(true);
  const closePasswordModal =() => setPasswordModal(false);
  const handleDeleteModal =() => setDeleteModal(true);
  const closeDeleteModal =() => setDeleteModal(false);
  const handleProfileImgModal =() => setProfileImgModal(true);
  const closeProfileImgModal =() => setProfileImgModal(false);

  const passModalFooter=(
  <Button 
    className="btn_close" 
    variant="secondary" 
    onClick={closePasswordModal}
  >
      닫기
  </Button>);
  const deleteModalFooter=(
  <Button 
    className='btn_close'
    variant='secondary'
    onClick={closeDeleteModal}
  >
    닫기
  </Button>);
  const profileImgModalFooter=(
  <Button
    className='btn_close'
    variant='secondary'
    onClick={closeProfileImgModal}
  >
    닫기
  </Button>);

  return (
    <Container className="my-page-container py-4">
      <Card className="p-4 shadow-sm">
        <Row className="align-items-center">
          <Col xs={12} md={4} className="text-center mb-3 mb-md-0">
            <UserProfileImage/>
          </Col>
          <Col xs={12} md={8}>
            <User user={user} isAuthenticated={isAuthenticated}/>
          </Col>
        </Row>

        <hr />
        <Row className="mt-3 g-2">
          <Col xs={12} md="auto">
            <LogoutButton />
          </Col>
          <Col xs={12} md="auto">
            <Button variant="warning" onClick={() => navigate('/user/update')}>
              회원정보 수정
            </Button>
          </Col>
          <Col xs={12} md="auto">
            <Button variant="primary" onClick={handleProfileImgModal}>
              프로필 이미지 수정
            </Button>
          </Col>
          <Col xs={12} md="auto">
            <Button variant="secondary" onClick={handlePasswordModal}>
              비밀번호 변경
            </Button>
          </Col>
          <Col xs={12} md="auto">
            <Button variant="danger" onClick={handleDeleteModal}>
              회원 탈퇴
            </Button>
          </Col>
        </Row>
      </Card>

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
          <CustomModal
            title={'프로필 이미지 수정'}
            modalState={profileImgModal}
            close={closeProfileImgModal}
            component={<UserProfileImageForm/>}
            footer={profileImgModalFooter}
          />
    </Container>
  );
}

export default MyPage;