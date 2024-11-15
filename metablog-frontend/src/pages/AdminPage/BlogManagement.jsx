import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { FiUser } from "react-icons/fi";
import axios from "axios";

const BASE_URL = process.env.REACT_APP_BASE_URL;

const BlogManagement = () => {
  const [activeTab, setActiveTab] = useState("pending");
  const [blogs, setBlogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const accessToken = localStorage.getItem("accessToken");
  const options = { year: "numeric", month: "long", day: "numeric" };

  useEffect(() => {
    fetchBlogs();
  }, [activeTab]);

  const fetchBlogs = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${BASE_URL}/admin/blogs/${activeTab}`);
      setBlogs(response.data.data);
    } catch (error) {
      console.error("Error fetching blogs:", error);
    }
    setLoading(false);
  };

  const changeBlogStatus = async (blogId, newStatus) => {
    try {
      await axios.put(
        `${BASE_URL}/admin/blogs/update-status`,
        { status: newStatus, blogId: blogId },
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );
      fetchBlogs();
    } catch (error) {
      console.error("Error changing blog status:", error);
    }
  };

  const renderTable = () => (
    <div className="overflow-x-auto shadow-md rounded-lg">
      <table className="w-full bg-white">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              ID
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Title
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Status
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Date
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Author
            </th>
            {activeTab === "pending" && (
              <>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Approve
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Reject
                </th>
              </>
            )}
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-200">
          {blogs.map((blog) => (
            <tr key={blog.id} className="hover:bg-gray-50">
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {blog.id}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                {blog.title}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {blog.status}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {new Date(blog.createdOn).toLocaleDateString(
                  undefined,
                  options
                )}
              </td>
              <td className="px-6 py-4 whitespace-nowrap">
                <div className="flex items-center">
                  <img
                    src={blog.imageUrl || "https://via.placeholder.com/40"}
                    alt="writer"
                    className="w-8 h-8 rounded-full mr-2"
                  />
                  <span className="text-sm text-gray-900">
                    {blog.author || "Unknown"}
                  </span>
                </div>
              </td>
              {activeTab === "pending" && (
                <>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button
                      className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded transition duration-300 ease-in-out"
                      onClick={() => changeBlogStatus(blog.id, "approved")}
                    >
                      Approve
                    </button>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button
                      className="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded transition duration-300 ease-in-out"
                      onClick={() => changeBlogStatus(blog.id, "rejected")}
                    >
                      Reject
                    </button>
                  </td>
                </>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );

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
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex space-x-8" aria-label="Tabs">
              {["pending", "approved", "rejected"].map((tab) => (
                <button
                  key={tab}
                  className={`
                    whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm
                    ${
                      activeTab === tab
                        ? "border-indigo-500 text-indigo-600"
                        : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
                    }
                  `}
                  onClick={() => setActiveTab(tab)}
                >
                  {tab.charAt(0).toUpperCase() + tab.slice(1)} Blogs
                </button>
              ))}
            </nav>
          </div>
          <div className="mt-6">
            {loading ? (
              <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-32 w-32 border-t-2 border-b-2 border-indigo-500"></div>
              </div>
            ) : (
              renderTable()
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default BlogManagement;
