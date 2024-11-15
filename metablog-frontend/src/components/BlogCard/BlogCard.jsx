import React from "react";
import { Avatar, AvatarImage, AvatarFallback } from "@radix-ui/react-avatar";
import {
  ClockIcon,
  CheckCircleIcon,
  XCircleIcon,
  BookmarkSlashIcon,
} from "@heroicons/react/24/outline";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const BlogCard = ({ blog, showStatus, isSavedBlog, onUnsave }) => {
  const navigate = useNavigate();

  const handleCardClick = (blogId) => {
    navigate(`/blog/${blogId}`);
  };

  const handleUnsave = (e) => {
    e.stopPropagation();
    onUnsave();
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case "PENDING":
        return <ClockIcon className="w-5 h-5 text-yellow-500" />;
      case "APPROVED":
        return <CheckCircleIcon className="w-5 h-5 text-green-500" />;
      case "REJECTED":
        return <XCircleIcon className="w-5 h-5 text-red-500" />;
      default:
        return null;
    }
  };
  const options = { year: "numeric", month: "long", day: "numeric" };

  return (
      <div
          className="card border rounded-lg shadow-lg overflow-hidden relative"
          onClick={() => handleCardClick(blog.id)}
      >
        <img
            src={blog.imageUrl}
            alt={blog.title}
            className="w-full h-40 object-cover"
        />
        {isSavedBlog && (
            <button
                onClick={handleUnsave}
                className="absolute bottom-4 right-5 bg-white rounded-full p-2 shadow-md hover:bg-gray-100"
            >
              <BookmarkSlashIcon className="w-6 h-6 text-red-500" />
            </button>
        )}
        <div className="card-content p-4">
          <h3 className="text-sm font-bold mt-2">{blog.title}</h3>
          <div className="flex items-center space-x-2 mt-2">
            <Avatar>
              <AvatarImage
                  src={blog.author_image_url}
                  className="w-6 h-6 rounded-full"
              />
              <AvatarFallback className="w-6 h-6 rounded-full bg-gray-300 text-center">
                {blog.author}
              </AvatarFallback>
            </Avatar>
            <span className="text-sm font-medium">{blog.author}</span>
            <span className="text-xs text-gray-500">
            {new Date(blog.createdOn).toLocaleDateString(undefined, options)}
          </span>
          </div>
          {showStatus && (
              <div className="flex items-center space-x-1 mt-2">
            <span
                className={`text-xs ${
                    blog.status === "PENDING"
                        ? "inline-flex items-center px-3 py-1 rounded-full text-sm font-semibold bg-yellow-100 text-yellow-800"
                        : blog.status === "APPROVED"
                            ? "inline-flex items-center px-3 py-1 rounded-full text-sm font-semibold text-green-500 bg-green-100"
                            : "inline-flex items-center px-3 py-1 rounded-full text-sm font-semibold text-red-500 bg-red-100"
                }`}
            >
              {blog.status}
              {getStatusIcon(blog.status)}
            </span>
              </div>
          )}
        </div>
      </div>
  );
};

export default BlogCard;