import { useEffect, useState } from "react";
import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";
import BlogCard from "../../components/BlogCard/BlogCard";
import Avatar from "react-avatar";
import axios from "axios";
import { AvatarFallback, AvatarImage } from "@radix-ui/react-avatar";

function BlogsListing() {
  const [blogs, setBlogs] = useState([]);
  const base_url = process.env.REACT_APP_BASE_URL;
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchBlogs = async () => {
      try {
        const token = localStorage.getItem("accessToken"); // Retrieve the token from local storage
        const response = await axios.get(`${base_url}/blogs/all-blogs`, {
          headers: {
            Authorization: `Bearer ${token}`, // Set the authorization header
          },
        });
        setBlogs(response.data.data);
      } catch (error) {
        console.error("Error fetching blogs:", error);
        setError("Error fetching blogs. Please try again later.");
      }
    };

    fetchBlogs();
  }, []);
  const formatDate = (timestamp) => {
    const options = { year: "numeric", month: "long", day: "numeric" };
    const date = new Date(timestamp);
    return date.toLocaleDateString(undefined, options);
  };

  return (
    <div className=" min-h-screen justify-center items-center w-full">
      <Header />
      <main
        className="p-4 md:p-10"
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        <div
          className="grid gap-4 w-10/12"
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            flexDirection: "column",
          }}
        >
          {error && <div className="text-red-500">{error}</div>}
          {blogs.length > 0 && (
            <div
              className="relative card"
              style={{ width: "80vw", height: "50vh" }}
            >
              <img
                src={blogs[0].imageUrl}
                alt="Featured Blog"
                style={{
                  width: "100%",
                  height: "100%",
                  objectFit: "cover",
                  overflow: "hidden",
                }}
              />
              <div className="absolute bottom-4 text-white">
                {/* <span className="badge">{blogs[0].category}</span> */}
                <h2 className="text-xl font-bold">{blogs[0].title}</h2>
                <div className="flex items-center space-x-2 mt-2">
                  <Avatar>
                    <AvatarImage
                      src={blogs[0].author_image_url}
                      className="w-6 h-6 rounded-full"
                    />
                    <AvatarFallback className="w-6 h-6 rounded-full bg-gray-300 text-center">
                      {blogs[0].authorInitials}
                    </AvatarFallback>
                  </Avatar>
                  <span className="text-sm font-medium">{blogs[0].author}</span>
                  <span className="text-xs text-gray-500">
                    {" "}
                    {formatDate(blogs[0].createdOn)}
                  </span>
                  
                </div>
              </div>
            </div>
          )}
          <div
            className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 mt-8"
            style={{ width: "80vw" }}
          >
            {blogs.slice(1).map((blog) => (
              <BlogCard key={blog.id} blog={blog} />
            ))}
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}

export default BlogsListing;
