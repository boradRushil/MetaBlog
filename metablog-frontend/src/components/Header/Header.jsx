import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { FiSearch, FiUser } from "react-icons/fi";
import { useState } from "react";
import Swal from "sweetalert2";
import axios from "axios";

const Header = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const navigate = useNavigate();
  const base_url = process.env.REACT_APP_BASE_URL;

  const handleInputChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      try {
        const token = localStorage.getItem("accessToken");
        const response = await axios.get(
          `${base_url}/blogs/search?title=${searchTerm}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        if (response.data.success) {
          navigate(`/search-result/${searchTerm}`, { state: { searchResults:response.data.data } });
        } else {
          Swal.fire({
            icon: "error",
            title: "Error",
            text:
              response.data.message ||
              "Failed to search for blogs. Please try again.",
          });
        }
      } catch (error) {
        console.error("Error searching for blogs: ", error);
        Swal.fire({
          icon: "error",
          title: "Error",
          text: "Failed to login. Please try again.",
        });
      }
    }
  };

  return (
    <header className="flex items-center justify-between h-16 px-4 border-b md:px-6 bg-white">
      <div className="flex items-center space-x-10">
        <Link
          to="/blogs-listing"
          className="flex items-center gap-2 text-lg font-semibold"
        >
          <img
            src="/logo-black.svg"
            alt="MetaBlog Logo"
            className="w-25 h-25"
          />
        </Link>
      </div>
      <div>
        <form className="relative w-96" onSubmit={handleFormSubmit}>
          <FiSearch className="absolute left-2.5 top-2.5 h-4 w-4" />
          <input
            type="search"
            placeholder="Search"
            className="pl-8 py-1 w-96 border rounded"
            value={searchTerm}
            onChange={handleInputChange}
          />
        </form>
      </div>
      <div className="flex items-center space-x-4">
        <Link to="/blogs-listing" className="text-gray-600">
          Home
        </Link>
        <Link to="/user-blogs" className="text-gray-600">
          Blogs
        </Link>
        <Link to="/create-blog" className="text-gray-600">
          Create Blog
        </Link>
        <Link to="/user-profile" className="text-gray-600">
          <button className="rounded-full p-2 bg-gray-200">
            <FiUser className="w-6 h-6 text-gray-600" />
          </button>
        </Link>
      </div>
    </header>
  );
};

export default Header;
