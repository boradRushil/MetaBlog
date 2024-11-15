import { useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import FrameComponent from "../../components/FrameComponent/FrameComponent";
import axios from "axios";
import Swal from "sweetalert2";

const ResetPasswordStep = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { email } = location.state || {};
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [passwordType, setPasswordType] = useState("password");
  const [confirmPasswordType, setConfirmPasswordType] = useState("password");
  const base_url = process.env.REACT_APP_BASE_URL;

  useEffect(() => {
    if (!email) {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Email not provided. Please try again.',
      }).then(() => {
        navigate("/forgot-password");
      });
    }
  }, [email, navigate]);

  const handleBackToLoginClick = () => {
    navigate("/login");
  };

  const handleResetPasswordClick = async () => {
    if (!isFormValid) return;

    try {
      const response = await axios.post(`${base_url}/auth/reset-password`, {
        email,
        newPassword: password
      });

      if (response.status === 200) {
        Swal.fire({
          icon: 'success',
          title: 'Success!',
          text: 'Password has been reset successfully.',
        }).then(() => {
          navigate("/login");
        });
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: response.data.message || 'Failed to reset password. Please try again.',
        });
      }
    } catch (error) {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: error.response?.data?.message || 'An error occurred while resetting the password. Please try again.',
      });
    }
  };

  const togglePasswordVisibility = () => {
    setPasswordType(passwordType === "password" ? "text" : "password");
  };

  const toggleConfirmPasswordVisibility = () => {
    setConfirmPasswordType(confirmPasswordType === "password" ? "text" : "password");
  };

  const isFormValid = password && confirmPassword && password === confirmPassword;

  return (
    <div className="flex flex-col md:flex-row w-full h-screen">
      <FrameComponent />
      <div className="flex flex-col items-center justify-center w-full md:w-1/2 p-8 md:p-20 bg-white">
        <div className="w-full max-w-md">
          <h1 className="text-4xl font-bold mb-6 text-gray-900">Reset Password</h1>
          <p className="text-lg mb-4 text-gray-700">Choose a new password for your account</p>
          <form className="space-y-6">
            <div className="relative">
              <input
                className="w-full p-3 border border-gray-300 rounded text-gray-900 placeholder-gray-500 text-base"
                placeholder="Your new password"
                type={passwordType}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
              <img
                className="absolute top-3 right-3 w-5 h-5 cursor-pointer"
                alt="toggle visibility"
                src="/iconoutlineeyeoff.svg"
                onClick={togglePasswordVisibility}
              />
            </div>
            <div className="relative">
              <input
                className="w-full p-3 border border-gray-300 rounded text-gray-900 placeholder-gray-500 text-base"
                placeholder="Confirm your new password"
                type={confirmPasswordType}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
              <img
                className="absolute top-3 right-3 w-5 h-5 cursor-pointer"
                alt="toggle visibility"
                src="/iconoutlineeyeoff.svg"
                onClick={toggleConfirmPasswordVisibility}
              />
            </div>
            <button
              type="button"
              className={`w-full py-3 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700 transition text-base ${!isFormValid ? 'opacity-50 cursor-not-allowed' : ''}`}
              onClick={handleResetPasswordClick}
              disabled={!isFormValid}
            >
              Reset Password
            </button>
            <button
              type="button"
              className="w-full py-3 mt-4 rounded border border-gray-300 bg-gray-100 text-gray-900 font-semibold hover:bg-gray-200 transition text-base"
              onClick={handleBackToLoginClick}
            >
              Back to Login
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ResetPasswordStep;
