import { Modal, Button } from 'react-bootstrap';
import PropTypes from 'prop-types';

function CustomModal({ title, children, footer, show, onHide, size = 'md', centered = true }) {
  return (
    <Modal show={show} onHide={onHide} size={size} centered={centered}>
      <Modal.Header closeButton>
        <Modal.Title>{title}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {children}
      </Modal.Body>
      <Modal.Footer>
        {footer ? footer : (
          <Button variant="secondary" onClick={onHide}>
            닫기
          </Button>
        )}
      </Modal.Footer>
    </Modal>
  );
}

CustomModal.propTypes = {
  title: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
  footer: PropTypes.node,
  show: PropTypes.bool.isRequired,
  onHide: PropTypes.func.isRequired,
  size: PropTypes.string,
  centered: PropTypes.bool,
};

export default CustomModal;