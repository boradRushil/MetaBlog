import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";
import BlogList from './BlogList';
import { useEffect } from "react";
import axios from "axios";  
import { useState } from "react";


const UserBlogs = () => {
    const [userBlogs, setUserBlogs] = useState([]);
    const [savedBlogs, setSavedBlogs] = useState([]);
    const [error, setError] = useState(null);
    const token = localStorage.getItem("accessToken");
    const [userDetails, setUserDetails] = useState({});
    const base_url = process.env.REACT_APP_BASE_URL;

    useEffect(() => {
        const fetchUserBlogs = async () => {
            try {
                const userBlogsResponse = await axios.get(`${base_url}/blogs/my-blogs`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setUserBlogs(userBlogsResponse.data.data);

                const savedBlogsResponse = await axios.get(`${base_url}/user/saved-blogs`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setSavedBlogs(savedBlogsResponse.data.data);
            } catch (error) {
                setError("Error fetching blogs. Please try again later.");
            }
        };
        const fetchuserDetails = async () => {
            try{
                axios.get(`${base_url}/user/details`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }).then(response => {
                    setUserDetails(response.data.data);
                }).catch(error => {
                    setError("Error fetching user details. Please try again later.");
                });
            } catch (error) {
                setError("Error fetching user details. Please try again later.");
            }
        };
        fetchUserBlogs();
        fetchuserDetails();

    }, []);

    const handleUnsave = async (blogId) => {
        try {
            await axios.delete(`${base_url}/user/remove-saved-blog/${blogId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSavedBlogs(savedBlogs.filter(blog => blog.id !== blogId));
        } catch (error) {
            setError("Error unsaving blog. Please try again later.");
        }
    };
    return (
        <div>
            <Header />
            <main className="container mx-auto px-4">
                <BlogList myBlogs={userBlogs}
                          savedBlogs={savedBlogs}
                          onUnsave={handleUnsave}
                          userDetails={userDetails} />
            </main>
            <Footer />
        </div>
    );
};

export default UserBlogs;
