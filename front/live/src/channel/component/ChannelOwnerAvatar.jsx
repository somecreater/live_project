import React, { useEffect, useState } from 'react';
import { Image } from 'react-bootstrap';
import ApiService from '../../common/api/ApiService';
import PropTypes from 'prop-types';
import { FaUserCircle } from 'react-icons/fa';

const ChannelOwnerAvatar = ({ userId, size = 30 }) => {
    const [imageUrl, setImageUrl] = useState(null);

    useEffect(() => {
        let isMounted = true;
        if (userId) {
            ApiService.profile_image.read_image(userId)
                .then(response => {
                    if (isMounted && response.data && response.data.image_url) {
                        setImageUrl(response.data.image_url);
                    }
                })
                .catch(() => {
                    // Keep fallback
                });
        }
        return () => { isMounted = false; };
    }, [userId]);

    if (imageUrl) {
        return (
            <Image
                src={imageUrl}
                roundedCircle
                className="me-2"
                style={{ width: `${size}px`, height: `${size}px`, objectFit: 'cover' }}
                alt="Owner"
            />
        );
    }

    return <FaUserCircle className="me-2 text-secondary" style={{ fontSize: `${size}px` }} />;
};

ChannelOwnerAvatar.propTypes = {
    userId: PropTypes.string,
    size: PropTypes.number
};

export default ChannelOwnerAvatar;  
