import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import InputFields from '../../components/RegisterInputFields/InputFields';
import axios from 'axios';
import Swal from 'sweetalert2';

const SignUp = () => {
  const navigate = useNavigate();
  const BASE_URL = process.env.REACT_APP_BASE_URL;

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
    isChecked: false,
    comingFrom: 'signup',
  });

  const [validationMessages, setValidationMessages] = useState({});
  const [errorMessage, setErrorMessage] = useState('');

  const handleLoginClick = () => {
    navigate('/login');
  };

  const isFormValid = () => {
    const { firstName, lastName, email, password, confirmPassword, isChecked } = formData;
    const combinedNameLength = (firstName + lastName).trim().length;
    return (
        combinedNameLength >= 4 &&
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email) &&
        password.trim() !== '' &&
        confirmPassword.trim() !== '' &&
        isChecked &&
        password === confirmPassword
    );
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!isFormValid()) {
      Swal.fire({
        icon: 'error',
        title: 'Invalid Form',
        text: 'Please fill out all fields correctly.',
      });
      return;
    }

    const registerData = {
      username: `${formData.firstName} ${formData.lastName}`,
      email: formData.email,
      password: formData.password,
      role: 'User', // Assuming the role is fixed as 'User'
    };

    try {
      const response = await axios.post(`${BASE_URL}/auth/register`, registerData);

      if (response.status === 201) {
        // Handle successful registration
        console.log('User registered successfully:', response.data);
        Swal.fire({
          icon: 'success',
          title: 'Registration Successful',
          text: 'Please check your email. We have sent you an OTP for verification.',
        }).then(() => {
          navigate('/verify-otp', { state: { email: formData.email, comingFrom: formData.comingFrom } }); // Pass email in state
        });
      } else {
        // Handle registration errors
        Swal.fire({
          icon: 'error',
          title: 'Registration Failed',
          text: response.data.message || 'An error occurred. Please try again.',
        });
      }
    } catch (error) {
      if (error.response) {
        // The request was made and the server responded with a status code
        Swal.fire({
          icon: 'error',
          title: 'Oops!',
          text: error.response.data.message,
        });
      } else if (error.request) {
        // The request was made but no response was received
        setErrorMessage('No response received from the server. Please try again.');
      } else {
        // Something happened in setting up the request that triggered an Error
        setErrorMessage('An error occurred during registration. Please try again.');
      }
      console.error('An error occurred during registration:', error);
    }
  };

  return (
      <div className="flex flex-col md:flex-row w-full h-screen">
        <div className="relative w-full md:w-1/2">
          <img className="absolute inset-0 w-full h-full object-cover" src="/background.svg" alt="background"/>
          <div className="relative flex flex-col items-start justify-start p-20 h-full">
            <img className="w-40 mb-10" src="/logo.svg" alt="MetaBlog Logo"/>
            <h1 className="text-4xl font-bold text-white">Blogs to dive into tech</h1>
          </div>
        </div>
        <div className="flex flex-col items-center justify-center w-full md:w-1/2 p-8 md:p-20 bg-white">
          <div className="w-full max-w-md">
            <h1 className="text-3xl font-bold mb-6 text-gray-900">Create Account</h1>
            <p className="text-base mb-4 text-gray-700">
              Already have an account?{' '}
              <span className="text-blue-600 cursor-pointer" onClick={handleLoginClick}>
              Login
            </span>
            </p>
            {errorMessage && <div className="text-red-500 mb-4">{errorMessage}</div>}
            <form className="space-y-6" onSubmit={handleSubmit}>
              <InputFields
                  formData={formData}
                  setFormData={setFormData}
                  validationMessages={validationMessages}
                  setValidationMessages={setValidationMessages}
              />
              <button
                  className={`w-full py-3 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700 transition ${!isFormValid() ? 'opacity-50 cursor-not-allowed' : ''}`}
                  type="submit"
                  disabled={!isFormValid()}
              >
                Create Account
              </button>
            </form>
            {errorMessage && <div className="mt-4 text-red-500">{errorMessage}</div>}
          </div>
        </div>
      </div>);
};

export default SignUp;
