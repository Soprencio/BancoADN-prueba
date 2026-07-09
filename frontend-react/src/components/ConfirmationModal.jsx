import React, { useState } from 'react';
import PropTypes from 'prop-types';
import './ConfirmationModal.css';

const ConfirmationModal = ({ isOpen, onClose, title, message, confirmLabel, cancelLabel, onConfirm, confirmVariant = 'primary' }) => {
  const [submitting, setSubmitting] = useState(false);

  if (!isOpen) return null;

  const handleConfirm = async () => {
    setSubmitting(true);
    try {
      await onConfirm();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className={`modal-backdrop${submitting ? ' loading' : ''}`}>
      <div className="modal-content">
        <h2>{title}</h2>
        <p>{message}</p>
        <div className="modal-actions">
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onClose}
            disabled={submitting}
          >
            {cancelLabel || 'Cancelar'}
          </button>
          <button
            type="button"
            className={`btn btn-${confirmVariant}`}
            onClick={handleConfirm}
            disabled={submitting}
          >
            {submitting ? 'Enviando...' : (confirmLabel || 'Confirmar')}
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
  confirmVariant: PropTypes.oneOf(['primary', 'danger', 'secondary', 'success']),
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