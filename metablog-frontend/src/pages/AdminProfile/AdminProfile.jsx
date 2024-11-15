import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { FiUser } from "react-icons/fi";
import {
  EyeIcon,
  EyeSlashIcon,
  ArrowLeftOnRectangleIcon,
} from "@heroicons/react/24/outline";
import axios from "axios";
import "./AdminProfile.css";
import Footer from "../../components/Footer/Footer";

const UserProfile = () => {
  const [profileImage, setProfileImage] = useState(null);
  const [profileImageUrl, setProfileImageUrl] = useState("https://via.placeholder.com/40");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [bio, setBio] = useState("");
  const [giturl, setGiturl] = useState("");
  const [linkedInurl, setLinkedInurl] = useState("");
  const [initialData, setInitialData] = useState({});
  const [isFormChanged, setIsFormChanged] = useState(false);

  const BASE_URL = process.env.REACT_APP_BASE_URL;
  const accessToken = localStorage.getItem("accessToken");

  useEffect(() => {
    fetchUserData();
  }, []);

  useEffect(() => {
    checkFormChanges();
  }, [username, email, bio, giturl, linkedInurl, profileImage]);

  const fetchUserData = async () => {
    try {
      const response = await axios.get(`${BASE_URL}/user/details`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      const userData = response.data.data;
      setUsername(userData.userName);
      setEmail(userData.email);
      setBio(userData.bio);
      setGiturl(userData.githubURL);
      setLinkedInurl(userData.linkedinURL);
      setProfileImageUrl(userData.imageURL || "/image.svg");
      setInitialData(userData);
    } catch (error) {
      console.error("Error fetching user data:", error);
    }
  };

  const checkFormChanges = () => {
    const isChanged =
      username !== initialData.userName ||
      email !== initialData.email ||
      bio !== initialData.bio ||
      giturl !== initialData.githubURL ||
      linkedInurl !== initialData.linkedinURL ||
      profileImage !== null;

    setIsFormChanged(isChanged);
  };

  const handleImageChange = (event) => {
    const file = event.target.files[0];
    setProfileImage(file);
    setProfileImageUrl(URL.createObjectURL(file));
  };

  const handleProfileUpdate = async (event) => {
    event.preventDefault();

    if (username.length < 4) {
      alert("Username should be at least 4 characters long");
      return;
    }

    try {
      const formData = new FormData();
      formData.append("userName", username);
      formData.append("email", email);
      formData.append("bio", bio);
      formData.append("githubURL", giturl);
      formData.append("linkedinURL", linkedInurl);
      if (profileImage) {
        formData.append("imageURL", profileImage);
      }

      const response = await axios.put(`${BASE_URL}/user/update`, formData, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "multipart/form-data",
        },
      });

      console.log("Profile updated successfully:", response.data.data);
      alert("Profile updated successfully");
      fetchUserData(); // Reload the user data
    } catch (error) {
      console.error("Error updating profile:", error);
      alert("Error updating profile. Please try again.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("role");
    window.location.href = "/login";
  };

  return (
    <div className="flex flex-col min-h-screen">
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
      <main className="w-3/4 max-w-xl mx-auto p-5 relative">
        <h2 className="text-2xl font-bold mb-5">User Profile</h2>
        <div className="text-center mb-8 ">
          <img
            src={profileImageUrl}
            alt="Profile"
            className="w-32 h-32 rounded-full mx-auto mb-2"
          />
          <label
            htmlFor="profileImage"
            className="text-blue-500 cursor-pointer"
          >
            Change profile photo
          </label>
          <input
            type="file"
            id="profileImage"
            accept="image/*"
            onChange={handleImageChange}
            className="hidden"
          />
        </div>
        <form onSubmit={handleProfileUpdate} className={`space-y-4 `}>
          <label className="block">
            Username
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full p-2 mt-1 border border-gray-300 rounded"
            />
          </label>
          <label className="block">
            Email
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full p-2 mt-1 border border-gray-300 rounded"
            />
          </label>

          <label className="block">
            Github
            <input
              type="text"
              value={giturl}
              onChange={(e) => setGiturl(e.target.value)}
              className="w-full p-2 mt-1 border border-gray-300 rounded"
            />
          </label>
          <label className="block">
            LinkedIn
            <input
              type="text"
              value={linkedInurl}
              onChange={(e) => setLinkedInurl(e.target.value)}
              className="w-full p-2 mt-1 border border-gray-300 rounded"
            />
          </label>
          <label className="block">
            Bio
            <textarea
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              className="w-full p-2 mt-1 border border-gray-300 rounded resize-vertical"
            />
          </label>
          <button
            type="submit"
            className={`px-4 py-2 bg-black text-white rounded ${
              !isFormChanged && "opacity-50 cursor-not-allowed"
            }`}
            disabled={!isFormChanged}
          >
            Update changes
          </button>
        </form>
        <div className="absolute top-5 right-5">
          <button
            onClick={handleLogout}
            className="flex items-center px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition-colors"
          >
            <ArrowLeftOnRectangleIcon className="w-5 h-5 mr-2" />
            Logout
          </button>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default UserProfile;
