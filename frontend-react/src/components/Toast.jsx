import React from 'react';
import { useToast } from '../contexts/ToastContext';
import './Toast.css';

const Toast = () => {
  const { toasts } = useToast();

  return (
    <div className="toast-container">
      {toasts.map((t) => (
        <div key={t.id} className={`toast toast-${t.type}`}>
          {t.message}
        </div>
      ))}
    </div>
  );
};

export default Toast;