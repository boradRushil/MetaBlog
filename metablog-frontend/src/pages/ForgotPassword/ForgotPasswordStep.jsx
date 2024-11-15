import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import FrameComponent from "../../components/FrameComponent/FrameComponent";
import axios from "axios";
import Swal from "sweetalert2";

const ForgotPasswordStep = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [isEmailValid, setIsEmailValid] = useState(false);
  const base_url = process.env.REACT_APP_BASE_URL;

  const handleBackToLoginClick = () => {
    navigate("/login");
  };

  const handleSendClick = async () => {
    if (isEmailValid) {
      try {
        const response = await axios.post(`${base_url}/auth/forget-password`, null, {
          params: { email },
          headers: {
            'Content-Type': 'application/json',
          },
        });

        if (response.status === 200) {
          Swal.fire({
            icon: 'success',
            title: 'Success!',
            text: 'OTP has been sent to your email successfully.',
          }).then(() => {
            navigate("/verify-otp", { state: { email } });
          });
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: response.data.message || 'Failed to send OTP. Please try again.',
          });
        }
      } catch (error) {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: error.response?.data?.message || 'An error occurred while sending the OTP. Please try again.',
        });
      }
    }
  };

  useEffect(() => {
    const emailRegex = /\S+@\S+\.\S+/;
    setIsEmailValid(emailRegex.test(email));
  }, [email]);

  return (
    <div className="flex flex-col md:flex-row w-full h-screen">
      <FrameComponent />
      <div className="flex flex-col items-center justify-center w-full md:w-1/2 p-8 md:p-20 bg-white">
        <div className="w-full max-w-md">
          <h1 className="text-4xl font-bold mb-6 text-gray-900">Forgot Password</h1>
          <p className="text-lg mb-4 text-gray-700">Enter your registered email address to get the OTP.</p>
          <form className="space-y-6">
            <input
              className="w-full p-3 border border-gray-300 rounded text-gray-900 placeholder-gray-500 text-base"
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <button
              type="button"
              className={`w-full py-3 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700 transition text-base ${!isEmailValid ? 'opacity-50 cursor-not-allowed' : ''}`}
              onClick={handleSendClick}
              disabled={!isEmailValid}
            >
              Send
            </button>
          </form>
          <button
            type="button"
            className="w-full py-3 mt-4 rounded border border-gray-300 bg-gray-100 text-gray-900 font-semibold hover:bg-gray-200 transition text-base"
            onClick={handleBackToLoginClick}
          >
            Back to Login
          </button>
        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordStep;