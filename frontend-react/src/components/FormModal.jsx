import React, { useState } from 'react';
import PropTypes from 'prop-types';
import './FormModal.css';

const FormModal = ({
  isOpen,
  onClose,
  title,
  fields,
  submitLabel = 'Enviar',
  onSubmit,
}) => {
  const [values, setValues] = useState({});
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  // Initialize values with empty strings
  // eslint-disable-next-line react-hooks/exhaustive-deps
  React.useEffect(() => {
    const init = {};
    fields.forEach((f) => {
      init[f.name] = '';
    });
    setValues(init);
  }, [fields]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setValues((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
    // clear error for this field
    setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const validate = () => {
    const newErrors = {};
    let valid = true;
    fields.forEach((f) => {
      const { name, required, type, pattern } = f;
      const value = values[name] ?? '';
      if (required && (value === '' || value === false)) {
        newErrors[name] = 'Este campo es obligatorio';
        valid = false;
      } else if (pattern && !new RegExp(pattern).test(value)) {
        newErrors[name] = 'Formato inválido';
        valid = false;
      }
    });
    setErrors(newErrors);
    return valid;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setSubmitting(true);
    try {
      await onSubmit(values);
      onClose();
    } catch (err) {
      // Could set error state; but we rely on toast from caller
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-backdrop">
      <div className="modal-content">
        <h2>{title}</h2>
        <form onSubmit={handleSubmit} className="form-modal">
          {fields.map((f) => (
            <div key={f.name} className="form-group">
              <label>{f.label}{f.required ? ' *' : ''}</label>
              {f.type === 'select' ? (
                <select
                  name={f.name}
                  value={values[f.name] || ''}
                  onChange={handleChange}
                  disabled={submitting}
                >
                  {f.options?.map((opt) => (
                    <option key={opt.value} value={opt.value}>
                      {opt.label}
                    </option>
                  ))}
                </select>
              ) : f.type === 'textarea' ? (
                <textarea
                  name={f.name}
                  value={values[f.name] || ''}
                  onChange={handleChange}
                  rows={f.rows || 3}
                  placeholder={f.placeholder}
                  disabled={submitting}
                />
              ) : f.type === 'checkbox' ? (
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name={f.name}
                    checked={!!values[f.name]}
                    onChange={handleChange}
                    disabled={submitting}
                  />
                  {f.label}
                </label>
              ) : (
                <input
                  type={f.type || 'text'}
                  name={f.name}
                  value={values[f.name] || ''}
                  onChange={handleChange}
                  placeholder={f.placeholder}
                  required={f.required}
                  disabled={submitting}
                />
              )}
              {errors[f.name] && (
                <span className="error-text">{errors[f.name]}</span>
              )}
            </div>
          ))}
          <div className="form-actions">
            <button
              type="button"
              onClick={onClose}
              disabled={submitting}
              className="btn btn-secondary"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="btn btn-primary"
            >
              {submitting ? 'Enviando...' : submitLabel}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

FormModal.propTypes = {
  isOpen: PropTypes.bool,
  onClose: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      type: PropTypes.oneOf([
        'text',
        'email',
        'password',
        'number',
        'textarea',
        'select',
        'checkbox',
      ]),
      placeholder: PropTypes.string,
      required: PropTypes.bool,
      options: PropTypes.arrayOf(
        PropTypes.shape({
          label: PropTypes.string.isRequired,
          value: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
            .isRequired,
        })
      ),
      pattern: PropTypes.string,
      rows: PropTypes.number,
    })
  ).isRequired,
  submitLabel: PropTypes.string,
  onSubmit: PropTypes.func.isRequired,
};

FormModal.defaultProps = {
  isOpen: false,
  submitLabel: 'Enviar',
  onSubmit: () => {},
};

export default FormModal;