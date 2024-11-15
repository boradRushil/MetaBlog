import { useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import FrameComponent from "../../components/FrameComponent/FrameComponent";
import axios from "axios";
import Swal from "sweetalert2";

const VerifyOTP = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [otp, setOtp] = useState("");
  const [otpVerified, setOtpVerified] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [email, setEmail] = useState("");
  const [isGoingToLogin, setIsGoingToLogin] = useState(false);
  const BASE_URL = process.env.REACT_APP_BASE_URL;

  useEffect(() => {
    if (location.state && location.state.email) {
      setEmail(location.state.email);
      setIsGoingToLogin(location.state.comingFrom);
    }
  }, [location.state]);

  const handleBackToLoginClick = () => {
    navigate("/reset-password-step-3");
  };

  const handleSendClick = () => {
    Swal.fire({
      icon: "success",
      title: "Email Sent",
      text: "Email resent successfully. Check your inbox for the OTP.",
    });
  };

  const handleVerifyOTPClick = async () => {
    try {
      const response = await axios.post(`${BASE_URL}/otp/verify`,
        { email, otp: parseInt(otp) },
        { headers: { "Content-Type": "application/json" } });

      if (response.status === 200) {
        setOtpVerified(true);
        await Swal.fire({
          icon: "success",
          title: "OTP Verified",
          text: response.data.message,
        });
        if (isGoingToLogin === "signup") {
          navigate("/login");
        } else {
          navigate("/reset-password-step-3", { state: { email } });
        }
      } else {
        setOtpVerified(false);
        await Swal.fire({
          icon: "error",
          title: "Error",
          text: response.data.message,
        });
      }
    } catch (error) {
      setOtpVerified(false);
      if (error.response) {
        setErrorMessage(error.response.data.message);
      } else {
        setErrorMessage("An error occurred during OTP verification. Please try again.");
      }
      console.error("An error occurred during OTP verification:", error);
    }
  };

  const handleOtpChange = (e) => {
    const { value } = e.target;
    if (/^\d*$/.test(value)) {
      setOtp(value);
      setOtpVerified(false);
    }
  };

  return (
    <div className="flex flex-col md:flex-row w-full h-screen">
      <FrameComponent />
      <div className="flex flex-col items-center justify-center w-full md:w-1/2 p-8 md:p-20 bg-white">
        <div className="w-full max-w-md">
          <button
            type="button"
            className="mb-4 py-2 px-4 rounded border border-blue-700 text-blue-700 hover:bg-blue-100 transition"
            onClick={handleSendClick}
          >
            Resend Email
          </button>
          <p className="text-sm mb-4 text-gray-700">We have sent a verification code to your registered email address</p>
          <div className="space-y-4">
            <p className="text-lg font-semibold mb-4 text-gray-700">OTP Verification Code</p>
            <div className="flex space-x-4">
              <input
                type="text"
                maxLength="6"
                className="w-full p-3 border border-gray-500 rounded text-gray-900"
                placeholder="Enter OTP"
                value={otp}
                onChange={handleOtpChange}
              />
              <button
                type="button"
                className={`py-3 px-6 rounded bg-blue-700 text-white font-bold hover:bg-blue-800 transition ${otp.length !== 6 ? 'opacity-50 cursor-not-allowed' : ''}`}
                onClick={handleVerifyOTPClick}
                disabled={otp.length !== 6}
              >
                Verify OTP
              </button>
            </div>
          </div>
          {errorMessage && <div className="text-red-500 mt-4">{errorMessage}</div>}
          <button
            type="button"
            className={`mt-4 w-full py-3 rounded border border-blue-800 text-blue-900 font-bold hover:bg-blue-100 transition ${!otpVerified ? 'opacity-50 cursor-not-allowed' : ''}`}
            onClick={handleBackToLoginClick}
            disabled={!otpVerified}
          >
            Proceed
          </button>
        </div>
      </div>
    </div>
  );
};

export default VerifyOTP;
