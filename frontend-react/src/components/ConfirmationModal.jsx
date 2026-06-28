import React from 'react';
import PropTypes from 'prop-types';
import './ConfirmationModal.css';

const ConfirmationModal = ({ isOpen, onClose, title, message, confirmLabel, cancelLabel, onConfirm, confirmVariant = 'primary' }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-backdrop">
      <div className="modal-content">
        <h2>{title}</h2>
        <p>{message}</p>
        <div className="modal-actions">
          <button
            type="button"
            className={`btn btn-${cancelLabel ? 'secondary' : 'secondary'}`}
            onClick={onClose}
          >
            {cancelLabel || 'Cancelar'}
          </button>
          <button
            type="button"
            className={`btn btn-${confirmVariant}`}
            onClick={onConfirm}
          >
            {confirmLabel || 'Confirmar'}
          </button>
        </div>
      </div>
    </div>
  );
};

ConfirmationModal.propTypes = {
  isOpen: PropTypes.bool,
  onClose: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
  message: PropTypes.string.isRequired,
  confirmLabel: PropTypes.string,
  cancelLabel: PropTypes.string,
  onConfirm: PropTypes.func,
  confirmVariant: PropTypes.oneOf(['primary', 'danger', 'secondary']),
};

ConfirmationModal.defaultProps = {
  isOpen: false,
  confirmLabel: 'Confirmar',
  cancelLabel: 'Cancelar',
  onConfirm: () => {},
  onClose: () => {},
  confirmVariant: 'primary',
};

export default ConfirmationModal;