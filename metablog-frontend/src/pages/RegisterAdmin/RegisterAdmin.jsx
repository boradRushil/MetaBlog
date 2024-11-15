import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import { FiUser } from "react-icons/fi";
import Swal from "sweetalert2";
import { useNavigate } from "react-router-dom";
import "./RegisterAdmin.css";

const RegisterAdmin = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [otp, setOtp] = useState("");
  const [showOtpField, setShowOtpField] = useState(false);
  const [isFormValid, setIsFormValid] = useState(false);
  const navigate = useNavigate();
  const base_url = process.env.REACT_APP_BASE_URL;
  const accessToken = localStorage.getItem("accessToken");

  useEffect(() => {
    const { firstName, lastName, email, password, confirmPassword } = formData;
    const isFormValid =
      firstName.length >= 2 &&
      lastName.length >= 2 &&
      email &&
      password &&
      confirmPassword &&
      password === confirmPassword;
    setIsFormValid(isFormValid);
  }, [formData]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "Passwords do not match",
      });
      return;
    }

    const registerData = {
      username: `${formData.firstName} ${formData.lastName}`,
      email: formData.email,
      password: formData.password,
      role: "Admin",
    };

    try {
      const response = await axios.post(
        `${base_url}/admin/blogs/register-admin`,
        registerData,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      if (response.data.success) {
        Swal.fire({
          icon: "success",
          title: "User Created",
          text:
            response.data.message ||
            "User created successfully. Please verify the OTP sent to your email.",
        });
        setShowOtpField(true);
      }
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text:
          error.response.data.message || "An error occurred. Please try again.",
      });
    }
  };

  const handleOtpSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(`${base_url}/otp/verify`, {
        email: formData.email,
        otp: otp,
      });
      if (response.data.success) {
        Swal.fire({
          icon: "success",
          title: "OTP Verified",
          text: "OTP verified successfully. Redirecting to admin home page.",
        });
        navigate("/admin-home");
      }
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: error.response.data.message || "Invalid OTP. Please try again.",
      });
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <Link
              to="/admin-home"
              className="flex items-center gap-2 text-lg font-semibold"
            >
              <img
                src="/logo-black.svg"
                alt="MetaBlog Logo"
                className="w-25 h-25"
              />
            </Link>
            <div className="flex items-center space-x-4">
              <Link
                to="/register-admin"
                className="text-gray-600 hidden sm:inline-block"
              >
                Register Admin
              </Link>
              <Link to="/admin-profile" className="text-gray-600">
                <button className="rounded-full p-2 bg-gray-200 hover:bg-gray-300 transition duration-300 ease-in-out">
                  <FiUser className="w-6 h-6 text-gray-600" />
                </button>
              </Link>
            </div>
          </div>
        </div>
      </header>

      <main className="max-w-md mx-auto mt-10 bg-white p-8 border border-gray-300 rounded-lg shadow-lg">
        <h1 className="text-2xl font-bold mb-6 text-center text-gray-800">
          Create Admin User
        </h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="text"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            placeholder="First Name"
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="text"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            placeholder="Last Name"
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="Email"
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="Password"
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="password"
            name="confirmPassword"
            value={formData.confirmPassword}
            onChange={handleChange}
            placeholder="Confirm Password"
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            type="submit"
            className={`w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 ${
              !isFormValid && "opacity-50 cursor-not-allowed"
            }`}
            disabled={!isFormValid}
          >
            Create User
          </button>
        </form>

        {showOtpField && (
          <form onSubmit={handleOtpSubmit} className="mt-6 space-y-4">
            <input
              type="text"
              name="otp"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              placeholder="Enter OTP"
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <button
              type="submit"
              className="w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
            >
              Verify OTP
            </button>
          </form>
        )}
      </main>
    </div>
  );
};

export default RegisterAdmin;
