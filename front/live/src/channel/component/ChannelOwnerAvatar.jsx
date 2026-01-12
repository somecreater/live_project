import React, { useEffect, useState } from 'react';
import { Image } from 'react-bootstrap';
import { imageCacheStore } from '../../common/context/imageCacheStore';
import PropTypes from 'prop-types';
import { FaUserCircle } from 'react-icons/fa';

const ChannelOwnerAvatar = ({ userId, size = 30 }) => {
    const [imageUrl, setImageUrl] = useState(null);
    const getImageUrl = imageCacheStore(state => state.getImageUrl);

    useEffect(() => {
        let isMounted = true;

        if (userId) {
            getImageUrl(userId).then(url => {
                if (isMounted && url) {
                    setImageUrl(url);
                }
            });
        }

        return () => { isMounted = false; };
    }, [userId, getImageUrl]);

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
