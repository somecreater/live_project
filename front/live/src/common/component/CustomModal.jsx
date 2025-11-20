import { Modal } from 'react-bootstrap';

function CustomModal(props){

  const {title, component, footer, modalState, close}=props;
  
  return (
    <Modal show={modalState} onHide={close}>
      <Modal.Header>
        <Modal.Title>{title}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {component}
      </Modal.Body>
      <Modal.Footer>
        {footer}
      </Modal.Footer>
    </Modal>
  );
}

export default CustomModal;