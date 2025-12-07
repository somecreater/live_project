import { useEffect, useState } from 'react';
import { userStateStore } from '../../common/context/userStateStore';
import User from '../component/User';
import { useNavigate } from 'react-router-dom';
import { Button, Card, Col, Container, Row } from 'react-bootstrap';
import PasswordForm from '../form/PasswordForm';
import CustomModal from '../../common/component/CustomModal';
import DeleteForm from '../form/DeleteForm';
import LogoutButton from '../component/LogoutButton';
import UserProfileImage from '../component/UserProfileImage';
import UserProfileImageForm from '../form/UserProfileImageForm';
import ChannelForm from '../../channel/form/ChannelForm';
import ChannelUpdateForm from '../../channel/form/ChannelUpdateForm';

function MyPage() {

  const { getUserInfo, getUserChannel, user, channel, isAuthenticated } = userStateStore();
  const [passwordModal, setPasswordModal] = useState(false);
  const [deleteModal, setDeleteModal] = useState(false);
  const [profileImgModal, setProfileImgModal] = useState(false);
  const [channelCreateModal, setChannelCreateModal] = useState(false);
  const [channelUpdateModal, setChannelUpdateModal] = useState(false);
  const navigate = useNavigate();

  useEffect(() => { getUserInfo(); getUserChannel(); }, []);
  const handlePasswordModal = () => setPasswordModal(true);
  const closePasswordModal = () => setPasswordModal(false);
  const handleDeleteModal = () => setDeleteModal(true);
  const closeDeleteModal = () => setDeleteModal(false);
  const handleProfileImgModal = () => setProfileImgModal(true);
  const closeProfileImgModal = () => setProfileImgModal(false);
  const handleChannelCreateModal = () => setChannelCreateModal(true);
  const closeChannelCreateModal = () => setChannelCreateModal(false);
  const handleChannelUpdateModal = () => setChannelUpdateModal(true);
  const closeChannelUpdateModal = () => setChannelUpdateModal(false);

  return (
    <Container className="my-page-container py-4">
      <Card className="p-4 shadow-sm">
        <Row className="align-items-center">
          <Col xs={12} md={4} className="text-center mb-3 mb-md-0">
            <UserProfileImage />
          </Col>
          <Col xs={12} md={8}>
            <User user={user} isAuthenticated={isAuthenticated} />
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
          {channel != null ?
            <Col xs={12} md="auto">
              <Button variant="primary" onClick={handleChannelUpdateModal}>
                채널 수정
              </Button>
            </Col> :
            <Col xs={12} md="auto">
              <Button variant="primary" onClick={handleChannelCreateModal}>
                채널 생성
              </Button>
            </Col>
          }
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
        title="회원 탈퇴"
        show={deleteModal}
        onHide={closeDeleteModal}
      >
        <DeleteForm />
      </CustomModal>

      <CustomModal
        title="비밀번호 변경"
        show={passwordModal}
        onHide={closePasswordModal}
      >
        <PasswordForm />
      </CustomModal>

      <CustomModal
        title="프로필 이미지 수정"
        show={profileImgModal}
        onHide={closeProfileImgModal}
      >
        <UserProfileImageForm />
      </CustomModal>
      <CustomModal
        title="신규 채널 생성"
        show={channelCreateModal}
        onHide={closeChannelCreateModal}
      >
        <ChannelForm />
      </CustomModal>
      <CustomModal
        title="채널 수정"
        show={channelUpdateModal}
        onHide={closeChannelUpdateModal}
      >
        <ChannelUpdateForm />
      </CustomModal>
    </Container>
  );
}

export default MyPage;