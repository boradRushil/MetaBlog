import React, { useState, useCallback } from "react";
import { Editor } from "@tinymce/tinymce-react";
import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";
import "./CreateBlog.css";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Swal from "sweetalert2";
import { useDropzone } from "react-dropzone";

const CreateBlog = () => {
  const [blogName, setBlogName] = useState("");
  const navigate = useNavigate();
  const [blogDescription, setBlogDescription] = useState("");
  const [blogContent, setBlogContent] = useState("");
  const [blogImage, setBlogImage] = useState(null);
  const [blogImageUrl, setBlogImageUrl] = useState("");
  const [blogTitle, setTitle] = useState("");
  const BASE_URL = process.env.REACT_APP_BASE_URL;
  const TINY_MCE_KEY = process.env.REACT_APP_TINY_MCE_KEY;
  console.log("TINY_MCE_KEY", TINY_MCE_KEY);
  const accessToken = localStorage.getItem("accessToken");

  const onDrop = useCallback((acceptedFiles) => {
    const file = acceptedFiles[0];
    setBlogImage(file);
    setBlogImageUrl(URL.createObjectURL(file));
  }, []);

  const { getRootProps, getInputProps } = useDropzone({
    onDrop,
    accept: "image/*",
    multiple: false,
  });

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData();
    formData.append("name", blogName);
    formData.append("title", blogTitle);
    formData.append("description", blogDescription);
    formData.append("content", blogContent);
    formData.append("image", blogImage);

    try {
      const response = await axios.post(
        `${BASE_URL}/blogs/create-blog`,
        formData,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "multipart/form-data",
          },
        }
      );
      console.log("Blog created successfully:", response.data);
      if(response.data.success) {
        Swal.fire({
            icon: "success",
            title: "Success!",
            text: response.data.message || "Blog created successfully.",
        });
        navigate("/blogs-listing");
        } else {
        Swal.fire({
            icon: "error",
            iconColor: "red",
            title: "Error",
            text: response.data.message || "Failed to create blog. Please try again.",
        });
        }
    } catch (error) {
      Swal.fire({
        icon: "error",
        iconColor: "red",
        title: "Oops...",
        text: "Something went wrong!",
        footer: `${
          error.response?.data?.message ||
          "An error occurred while creating the blog. Please try again."
        }`,
      });
    }
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-1 p-4 md:p-10 create-blog-container">
        <div className="create-blog-header">
          <h1>CREATE BLOG</h1>
        </div>
        <form onSubmit={handleSubmit} className="create-blog-form">
          <div className="form-group">
            <label htmlFor="blogName">Blog Name</label>
            <input
              id="blogName"
              type="text"
              value={blogName}
              onChange={(e) => setBlogName(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label htmlFor="title">Title</label>
            <input
              id="title"
              type="text"
              value={blogTitle}
              onChange={(e) => setTitle(e.target.value)}
            />
          </div>
          <div className="form-group" style={{ gridColumn: "span 2" }}>
            <label htmlFor="blogDescription">Blog Description</label>
            <textarea
              id="blogDescription"
              value={blogDescription}
              onChange={(e) => setBlogDescription(e.target.value)}
              rows="3"
            />
          </div>
          <div className="form-group blog-image-upload" style={{ gridColumn: "span 2" }}>
            <div
              {...getRootProps({
                className: `dropzone ${blogImage ? 'active' : ''}`
              })}
            >
              <input {...getInputProps()} />
              {blogImageUrl ? (
                <>
                  <img src={blogImageUrl} alt="Blog" />
                  <p>Click again to change the image</p>
                </>
              ) : (
                <p>Drag 'n' drop an image here, or click to select one</p>
              )}
            </div>
          </div>
          <div className="form-group" style={{ gridColumn: "span 2" }}>
            <label htmlFor="blogContent">Write Your Tech Ideas...</label>
            <Editor
              apiKey= {TINY_MCE_KEY}
              id="blogContent"
              initialValue="<p>Welcome to Meta blogs!!</p>"
              init={{
                height: 500,
                menubar: false,
                plugins:
                  "anchor autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount checklist mediaembed casechange export formatpainter pageembed linkchecker a11ychecker tinymcespellchecker permanentpen powerpaste advtable advcode editimage advtemplate mentions tableofcontents footnotes mergetags autocorrect typography inlinecss markdown",
                toolbar:
                  "undo redo | styles | bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image | help",
              }}
              onEditorChange={(content) => setBlogContent(content)}
            />
          </div>
          <div className="form-group submit-btn">
            <button type="submit">Submit Blog</button>
          </div>
        </form>
      </main>
      <Footer />
    </div>
  );
};

export default CreateBlog;
