import React from 'react';
import { Card, ListGroup, Badge } from 'react-bootstrap';
import PropTypes from 'prop-types';

const Channel = ({ channel }) => {
    if (!channel) {
        return <div className="alert alert-warning">Channel data is missing.</div>;
    }

    const { id, name, description, createdAt, updatedAt, user_login_id } = channel;

    return (
      <Card className="shadow-sm h-100">
        <Card.Header as="h5" className="d-flex justify-content-between align-items-center bg-primary text-white">
          <span>{name || 'Untitled Channel'}</span>
          <Badge bg="light" text="dark">ID: {id}</Badge>
        </Card.Header>
        <Card.Body>
          <Card.Title>Description</Card.Title>
          <Card.Text>
            {description || 'No description available.'}
          </Card.Text>
        </Card.Body>
        <ListGroup className="list-group-flush">
          <ListGroup.Item className="d-flex justify-content-between align-items-center">
            <strong>Owner:</strong>
            <span>{user_login_id || 'Unknown'}</span>
          </ListGroup.Item>
          <ListGroup.Item className="d-flex justify-content-between align-items-center">
            <strong>Created At:</strong>
            <span className="text-muted">{createdAt ? new Date(createdAt).toLocaleString() : 'N/A'}</span>
          </ListGroup.Item>
          <ListGroup.Item className="d-flex justify-content-between align-items-center">
            <strong>Updated At:</strong>
            <span className="text-muted">{updatedAt ? new Date(updatedAt).toLocaleString() : 'N/A'}</span>
          </ListGroup.Item>
        </ListGroup>
      </Card>
    );
};

Channel.propTypes = {
    channel: PropTypes.shape({
        id: PropTypes.number,
        name: PropTypes.string,
        description: PropTypes.string,
        createdAt: PropTypes.string,
        updatedAt: PropTypes.string,
        user_login_id: PropTypes.string,
    }),
};

export default Channel;