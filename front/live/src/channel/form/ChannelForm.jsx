import React, { useState } from 'react';
import { Form, Button, Container, Card, Alert } from 'react-bootstrap';
import { userStateStore } from '../../common/context/userStateStore';
import { useNavigate } from 'react-router-dom';
import ApiService from '../../common/api/ApiService';

function ChannelForm({ props }) {
    const { user } = userStateStore();
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        user_login_id: user.loginId,
    });
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        if (!formData.name || !formData.user_login_id) {
            setError('Name and User Login ID are required.');
            return;
        }
        const response = await ApiService.channel.create(formData);
        const data = response.data;
        if (data.result) {
            alert('채널이 생성되었습니다.');
            navigate('/my_channel');
        } else {
            alert('채널 생성에 실패했습니다.');
        }
    };

    return (
        <Container className="d-flex justify-content-center align-items-center mt-5">
            <Card className="shadow-lg p-4" style={{ width: '100%', maxWidth: '500px' }}>
                <Card.Body>
                    <Card.Title className="text-center mb-4">Create New Channel</Card.Title>

                    {error && <Alert variant="danger">{error}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3" controlId="formChannelName">
                            <Form.Label>채널 이름</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="채널 이름을 입력해주세요."
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-4" controlId="formDescription">
                            <Form.Label>채널 설명</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                placeholder="채널 설명을 입력해주세요."
                                name="description"
                                value={formData.description}
                                onChange={handleChange}
                            />
                        </Form.Group>

                        <div className="d-grid gap-2">
                            <Button variant="primary" type="submit" size="lg">
                                Create Channel
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};


export default ChannelForm;
