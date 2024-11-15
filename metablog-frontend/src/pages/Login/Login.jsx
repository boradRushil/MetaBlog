import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import FrameComponent from "../../components/FrameComponent/FrameComponent";
import axios from "axios";
import Swal from "sweetalert2";

const Login = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [isFormValid, setIsFormValid] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const base_url = process.env.REACT_APP_BASE_URL;

  const handleForgotPasswordClick = () => {
    navigate("/forgot-password-step-3");
  };

  const handleSignUpClick = () => {
    navigate("/");
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post(`${base_url}/auth/login`, formData);
      if (response.data.success) {
        const { accessToken, role } = response.data.data;

        // Store tokens in local storage
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('role', role);

        Swal.fire({
          icon: "success",
          title: "Success!",
          text: response.data.message || "Logged in successfully."
        })
        // Redirect based on role
        if (role === 'User') {
          navigate("/blogs-listing"); // Redirect to blogs listing page
        } else if (role === 'Admin') {
          navigate("/admin-home");  // Redirect to admin home page
        }
      } else {
        Swal.fire({
          icon: "error",
          title: "Error",
          text: response.data.message || "Failed to login. Please try again."
        });
      }
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "Failed to login. Please try again."
      });
    }
  }

  useEffect(() => {
    const validateForm = () => {
      const { email, password } = formData;
      const emailRegex = /\S+@\S+\.\S+/;
      setIsFormValid(emailRegex.test(email) && password.trim() !== '');
    };

    validateForm();
  }, [formData]);

  return (
    <div className="flex flex-col md:flex-row w-full h-screen">
      <FrameComponent />
      <div className="flex flex-col items-center justify-center w-full md:w-1/2 p-8 md:p-20 bg-white">
        <div className="w-full max-w-md">
          <h1 className="text-4xl font-bold mb-6 text-gray-900">Welcome Back</h1>
          <p className="text-lg mb-4 text-gray-700">
            Don’t have an account?{' '}
            <span className="text-blue-600 cursor-pointer font-semibold" onClick={handleSignUpClick}>
              Sign Up
            </span>
          </p>
          {errorMessage && <div className="text-red-500 mb-4 text-base">{errorMessage}</div>}
          <form className="space-y-6" onSubmit={handleLogin}>
            <div className="space-y-4">
              <input
                className="w-full p-3 border border-gray-300 rounded text-gray-900 placeholder-gray-500 text-base"
                name="email"
                placeholder="Email"
                type="text"
                value={formData.email}
                onChange={handleChange}
              />
              <div className="relative">
                <input
                  className="w-full p-3 border border-gray-300 rounded text-gray-900 placeholder-gray-500 text-base"
                  name="password"
                  placeholder="Password"
                  type={showPassword ? "text" : "password"}
                  value={formData.password}
                  onChange={handleChange}
                />
                <img
                  className="absolute top-3 right-3 w-5 h-5 cursor-pointer"
                  alt="toggle visibility"
                  src={showPassword ? "/iconoutlineeye.svg" : "/iconoutlineeyeoff.svg"}
                  onClick={() => setShowPassword(!showPassword)}
                />
              </div>
              <div
                className="text-base text-blue-600 cursor-pointer font-semibold"
                onClick={handleForgotPasswordClick}
              >
                Forgot Password
              </div>
            </div>
            <button
              className={`w-full py-3 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700 transition text-base ${!isFormValid ? 'opacity-50 cursor-not-allowed' : ''}`}
              type="submit"
              disabled={!isFormValid}
            >
              Login
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
