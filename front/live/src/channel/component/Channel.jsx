import React from 'react';
import { Card, Badge, OverlayTrigger, Tooltip } from 'react-bootstrap';
import PropTypes from 'prop-types';
import { FaArrowCircleRight, FaUsers } from 'react-icons/fa';
import ChannelOwnerAvatar from './ChannelOwnerAvatar';
import { useNavigate } from 'react-router-dom';

const formatNumber = (num) => {
  if (num === undefined || num === null) return '0';
  if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
  if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
  return num.toString();
};

const Channel = ({ channel }) => {
  if (!channel) return null;
  const navigate = useNavigate();
  const { id, name, description, createdAt, user_login_id, subscription_count } = channel;

  // Generate avatar initial
  const initial = name ? name.charAt(0).toUpperCase() : (user_login_id ? user_login_id.charAt(0).toUpperCase() : 'C');

  const handleEnterChannel = () => {
    navigate(`/channel/${id}`);
  };

  return (
    <Card className="channel-card h-100 shadow-sm">
      <div className="channel-card-header">
        <div className="channel-avatar">
          {initial}
        </div>
      </div>
      <Card.Body className="pt-5 mt-2">
        <div className="d-flex justify-content-between align-items-start mb-2">
          <Card.Title className="fw-bold mb-0 text-truncate" style={{ fontSize: '1.25rem', maxWidth: '80%' }}>
            {name || 'Untitled Channel'}
          </Card.Title>
          <Badge bg="light" text="secondary" className="border">#{id}</Badge>
        </div>

        <div className="mb-3 text-muted small d-flex align-items-center justify-content-between">
          <div className="d-flex align-items-center">
            <ChannelOwnerAvatar userId={user_login_id} size={24} />
            <strong className="ms-2">{user_login_id || 'Unknown Owner'}</strong>
          </div>
          <div className="d-flex align-items-center" title="Subscribers">
            <FaUsers className="me-1" />
            <span>{formatNumber(subscription_count) || 0}</span>
          </div>
        </div>

        <Card.Text className="text-muted text-truncate-2" style={{ minHeight: '3rem' }}>
          {description || 'No description available for this channel.'}
        </Card.Text>
      </Card.Body>
      <Card.Footer className="bg-white border-0 pt-0 pb-3">
        <div className="d-flex justify-content-between align-items-center small text-muted">
          <span>
            Created: {createdAt ? new Date(createdAt).toLocaleDateString() : 'Unknown'}
          </span>
          <OverlayTrigger
            placement="top"
            overlay={<Tooltip>Enter Channel</Tooltip>}
          >
            <span>
              <FaArrowCircleRight className="text-primary" style={{ fontSize: '1.5rem', cursor: 'pointer' }} onClick={handleEnterChannel} />
            </span>
          </OverlayTrigger>
        </div>
      </Card.Footer>
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
    subscription_count: PropTypes.number,
  }),
};

export default Channel;