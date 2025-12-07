import { Button, Form } from "react-bootstrap";
import { userStateStore } from "../../common/context/userStateStore";
import { useEffect, useState } from "react";
import ApiService from "../../common/api/ApiService";

function ChannelUpdateForm() {
  const { channel, getUserChannel } = userStateStore();
  useEffect(() => { getUserChannel(); }, []);
  const [updateChannel, setUpdateChannel] = useState({
    id: channel.id,
    name: channel.name,
    description: channel.description,
    createdAt: channel.createdAt,
    updatedAt: channel.updatedAt,
    user_login_id: channel.user_login_id
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUpdateChannel({ ...updateChannel, [name]: value });
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(updateChannel);
    const response = await ApiService.channel.update(updateChannel);
    const data = response.data;
    if (data.result) {
      alert("채널이 수정되었습니다.");
      getUserChannel();
    } else {
      alert("채널이 수정되지 않았습니다.");
    }
  };
  return (
    <Form onSubmit={handleSubmit}>
      <Form.Group controlId="name">
        <Form.Label>채널 이름</Form.Label>
        <Form.Control type="text" value={updateChannel.name} onChange={handleChange} />
      </Form.Group>
      <Form.Group controlId="description">
        <Form.Label>채널 설명</Form.Label>
        <Form.Control type="text" value={updateChannel.description} onChange={handleChange} />
      </Form.Group>
      <Form.Group controlId="user_login_id">
        <Form.Label>소유주 로그인 ID</Form.Label>
        <Form.Text type="text" value={updateChannel.user_login_id} />
      </Form.Group>
      <Button type="submit">수정</Button>
    </Form>
  );
}

export default ChannelUpdateForm;