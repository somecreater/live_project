import React from 'react';
import { Row, Col, Alert } from 'react-bootstrap';
import Channel from './Channel';
import PropTypes from 'prop-types';

const ChannelList = ({ channels }) => {
  if (!channels || channels.length === 0) {
    return <Alert variant="info" className="text-center">검색 결과가 없습니다.</Alert>;
  }

  return (
    <Row>
      {channels.map((channel) => (
        <Col key={channel.id} xs={12} md={6} lg={4} className="mb-4">
          <Channel channel={channel} />
        </Col>
      ))}
    </Row>
  );
};

ChannelList.propTypes = {
  channels: PropTypes.arrayOf(PropTypes.object).isRequired,
};

export default ChannelList;
