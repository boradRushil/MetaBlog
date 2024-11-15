import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

const InputFields = ({ formData, setFormData, validationMessages, setValidationMessages, className = '' }) => {
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [touchedFields, setTouchedFields] = useState({});

    const handleChange = (e) => {
        const { name, value, type, checked, files } = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: type === 'checkbox' ? checked : files ? files[0] : value,
        }));
    };

    const handleBlur = (e) => {
        const { name } = e.target;
        setTouchedFields((prevTouched) => ({ ...prevTouched, [name]: true }));
    };

    useEffect(() => {
        const validateFields = () => {
            const newValidationMessages = {};
            const { firstName, lastName, email, password, confirmPassword } = formData;
            const combinedNameLength = (firstName + lastName).trim().length;

            if (combinedNameLength < 4) {
                newValidationMessages.name = 'Name must be at least 4 characters long.';
            }

            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                newValidationMessages.email = 'Please enter a valid email address.';
            }

            if (password !== confirmPassword) {
                newValidationMessages.password = 'Passwords do not match.';
            }

            setValidationMessages(newValidationMessages);
        };

        const timeoutId = setTimeout(validateFields, 500);
        return () => clearTimeout(timeoutId);
    }, [formData, setValidationMessages]);

    return (
        <div className={`space-y-4 ${className}`}>
            <div className="flex space-x-4">
                <input
                    className="w-1/2 p-3 border border-gray-600 rounded text-gray-900"
                    placeholder="First Name"
                    type="text"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    onBlur={handleBlur}
                />
                <input
                    className="w-1/2 p-3 border border-gray-600 rounded text-gray-900"
                    placeholder="Last Name"
                    type="text"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    onBlur={handleBlur}
                />
            </div>
            {touchedFields.firstName && touchedFields.lastName && validationMessages.name && (
                <div className="text-red-500">{validationMessages.name}</div>
            )}
            <input
                className="w-full p-3 border border-gray-600 rounded text-gray-900"
                placeholder="Email"
                type="text"
                name="email"
                value={formData.email}
                onChange={handleChange}
                onBlur={handleBlur}
            />
            {touchedFields.email && validationMessages.email && (
                <div className="text-red-500">{validationMessages.email}</div>
            )}
            <div className="relative">
                <input
                    className="w-full p-3 border border-gray-600 rounded text-gray-900"
                    placeholder="Password"
                    type={showPassword ? 'text' : 'password'}
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    onBlur={handleBlur}
                />
                <img
                    className="absolute top-3 right-3 w-5 h-5 cursor-pointer"
                    alt="toggle visibility"
                    src="/iconoutlineeyeoff.svg"
                    onClick={() => setShowPassword(!showPassword)}
                />
            </div>
            <div className="relative">
                <input
                    className="w-full p-3 border border-gray-600 rounded text-gray-900"
                    placeholder="Confirm Password"
                    type={showConfirmPassword ? 'text' : 'password'}
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    onBlur={handleBlur}
                />
                <img
                    className="absolute top-3 right-3 w-5 h-5 cursor-pointer"
                    alt="toggle visibility"
                    src="/iconoutlineeyeoff.svg"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                />
            </div>
            {touchedFields.password && touchedFields.confirmPassword && validationMessages.password && (
                <div className="text-red-500">{validationMessages.password}</div>
            )}
            <div className="flex items-center space-x-3">
                <input
                    type="checkbox"
                    className="h-4 w-4 text-blue-700 border-gray-600 rounded"
                    name="isChecked"
                    checked={formData.isChecked}
                    onChange={handleChange}
                />
                <span className="text-gray-900 text-sm">
          I agree to MetaBlog <a href="#" className="text-blue-700">Terms of service</a> and <a href="#" className="text-blue-700">Privacy policy</a>
        </span>
            </div>
        </div>
    );
};

InputFields.propTypes = {
    formData: PropTypes.object.isRequired,
    setFormData: PropTypes.func.isRequired,
    validationMessages: PropTypes.object.isRequired,
    setValidationMessages: PropTypes.func.isRequired,
    className: PropTypes.string,
};

export default InputFields;
