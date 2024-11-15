import { useState } from "react";
import BlogCard from "../../components/BlogCard/BlogCard";
import { FaGithub, FaLinkedin, FaEnvelope } from "react-icons/fa";

const BlogList = ({ myBlogs, savedBlogs, userDetails , onUnsave}) => {
    const token = localStorage.getItem("accessToken");
    const BASE_URL = process.env.REACT_APP_BASE_URL;
    const [view, setView] = useState("myBlogs");
    const user = userDetails;

    const blogsToDisplay = view === "myBlogs" ? myBlogs : savedBlogs;

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="max-w-2xl mx-auto bg-gray-100 rounded-lg overflow-hidden shadow-md mb-8">
                <div className="p-6 text-center">
                    <img
                        src={user.imageURL}
                        alt={user.userName}
                        className="w-24 h-24 rounded-full mx-auto mb-4"
                    />
                    <h2 className="text-xl font-semibold">{user.userName}</h2>
                    <p className="text-gray-600 text-sm mb-4">Collaborator & Editor</p>
                    <p className="text-gray-800 mb-6">
                        {user.bio}
                    </p>
                    <div className="flex justify-center space-x-4">
                        {user.linkedinURL && (
                            <a href={user.linkedinURL} target="_blank" rel="noopener noreferrer"
                               className="text-gray-600 hover:text-gray-800">
                                <FaLinkedin size={20}/>
                            </a>
                        )}
                        {user.githubURL && (
                            <a href={user.githubURL} target="_blank" rel="noopener noreferrer"
                               className="text-gray-600 hover:text-gray-800">
                                <FaGithub size={20}/>
                            </a>
                        )}
                        {user.email && (
                            <a href={`mailto:${user.email}`} className="text-gray-600 hover:text-gray-800">
                                <FaEnvelope size={20}/>
                            </a>
                        )}
                    </div>
                </div>
            </div>

            <div className="flex justify-center items-center mb-4 md:p-5">
                <button
                    className={`px-4 py-2 ${view === 'myBlogs' ? 'bg-black text-white' : 'bg-gray-200 text-black'}`}
                    onClick={() => setView('myBlogs')}
                >
                    My Blogs
                </button>
                <button
                    className={`ml-2 px-4 py-2 ${view === 'savedBlogs' ? 'bg-black text-white' : 'bg-gray-200 text-black'}`}
                    onClick={() => setView('savedBlogs')}
                >
                    Saved Blogs
                </button>
            </div>
            {/* Blog cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                {blogsToDisplay.map((blog) => (
                    <BlogCard
                        key={blog.id}
                        blog={blog}
                        showStatus={view === 'myBlogs'}
                        isSavedBlog={view === 'savedBlogs'}
                        onUnsave={view === 'savedBlogs' ? () => onUnsave(blog.id) : null}
                    />
                ))}
            </div>
</div>
)
    ;
};

export default BlogList;