import { Form } from "react-bootstrap";
import { userStateStore } from "../../common/context/userStateStore";

function User({user, isAuthenticated}){

  
  if (!isAuthenticated) {
    return <p className="text-muted mt-3 text-center">비로그인 상태입니다.</p>;
  }
  return (
    <Form>
      <Form.Group className="mb-3">
        <Form.Label>아이디</Form.Label>
        <Form.Control type="text" value={user?.loginId || ""} readOnly />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>전화번호</Form.Label>
        <Form.Control type="text" value={user?.phone || ""} readOnly />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>닉네임</Form.Label>
        <Form.Control type="text" value={user?.nickname || ""} readOnly />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>로그인 타입</Form.Label>
        <Form.Control type="text" value={user?.loginType || ""} readOnly />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>권한 정보</Form.Label>
        <Form.Control type="text" value={user?.userType || ""} readOnly />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>계정 생성일자</Form.Label>
        <Form.Control type="text" value={user?.createdAt || ""} readOnly />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>수정 일자</Form.Label>
        <Form.Control type="text" value={user?.updatedAt || ""} readOnly />
      </Form.Group>
    </Form>
  );
}

export default User;