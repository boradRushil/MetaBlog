import { useEffect } from "react";
import {
  Routes,
  Route,
  useNavigationType,
  useLocation,
} from "react-router-dom";
import SignUp from "./pages/SignUp/SignUp";
import Login from "./pages/Login/Login";
import ForgotPasswordStep from "./pages/ForgotPassword/ForgotPasswordStep";
import ForgotPasswordStep1 from "./pages/ForgotPassword/ForgotPasswordStep1";
import ResetPasswordStep from "./pages/ResetPassword/ResetPasswordStep";
import ResetPasswordStep1 from "./pages/ResetPasswordStep1";
import "./index.css";
import BlogsListing from "./pages/BlogsListing/BlogsListing";
import BlogPage from "./pages/BlogPage/BlogPage";
import CreateBlog from "./pages/CreateBlog/CreateBlog";
import UserProfile from "./pages/UserProfile/UserProfile";
import UserBlogs from "./pages/Blogs/UserBlogs";
import SearchResult from "./pages/Search/SearchResult";
import BlogManagement from "./pages/AdminPage/BlogManagement";
import ProtectedRoute from "./components/RouteProtection/ProtectedRoute";
import AdminRoute from "./components/RouteProtection/AdminRoute";
import AdminProfile from "./pages/AdminProfile/AdminProfile";
import RegisterAdmin from "./pages/RegisterAdmin/RegisterAdmin";

function App() {
  const action = useNavigationType();
  const location = useLocation();
  const pathname = location.pathname;

  // You'll need to implement these functions to check the user's authentication status and role
  const isAuthenticated = () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      return false;
    }

    try {
      // Decode the token
      const base64Url = token.split(".")[1];
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split("")
          .map((c) => {
            return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
          })
          .join("")
      );

      const decodedToken = JSON.parse(jsonPayload);

      // Check if the token has expired
      const currentTime = Date.now() / 1000; // Convert to seconds
      if (decodedToken.exp < currentTime) {
        // Token has expired
        localStorage.removeItem("accessToken"); // Remove the expired token
        return false;
      }

      // Token is valid
      return true;
    } catch (error) {
      console.error("Error decoding token:", error);
      return false;
    }
  };

  const getUserRole = () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      return "guest";
    }

    try {
      const base64Url = token.split(".")[1];
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split("")
          .map((c) => {
            return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
          })
          .join("")
      );

      const decodedToken = JSON.parse(jsonPayload);

      return decodedToken.role || "user";
    } catch (error) {
      console.error("Error decoding token:", error);
      return "user"; // Default to 'user' if there's an error
    }
  };

  return (
    <Routes>
      <Route path="/" element={<SignUp />} />
      <Route path="/login" element={<Login />} />
      <Route path="/forgot-password-step-3" element={<ForgotPasswordStep />} />
      <Route path="/verify-otp" element={<ForgotPasswordStep1 />} />
      <Route path="/reset-password-step-3" element={<ResetPasswordStep />} />
      <Route path="/reset-password-step-4" element={<ResetPasswordStep1 />} />

      {/* Protected routes for authenticated users */}
      <Route
        path="/blogs-listing"
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated()}
            userRole={getUserRole()}
            allowedRoles={["User", "Admin"]}
          >
            <BlogsListing />
          </ProtectedRoute>
        }
      />
      <Route
        path="/blog/:blogId"
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated()}
            userRole={getUserRole()}
            allowedRoles={["User", "Admin"]}
          >
            <BlogPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/create-blog"
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated()}
            userRole={getUserRole()}
            allowedRoles={["User", "Admin"]}
          >
            <CreateBlog />
          </ProtectedRoute>
        }
      />
      <Route
        path="/user-profile"
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated()}
            userRole={getUserRole()}
            allowedRoles={["User", "Admin"]}
          >
            <UserProfile />
          </ProtectedRoute>
        }
      />
      <Route
        path="/user-blogs"
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated()}
            userRole={getUserRole()}
            allowedRoles={["User", "Admin"]}
          >
            <UserBlogs />
          </ProtectedRoute>
        }
      />
      <Route
        path="/search-result/:searchTerm"
        element={
          <ProtectedRoute
            isAuthenticated={isAuthenticated()}
            userRole={getUserRole()}
            allowedRoles={["User", "Admin"]}
          >
            <SearchResult />
          </ProtectedRoute>
        }
      />

      {/* Admin-only route */}
      <Route
        path="/admin-home"
        element={
          <AdminRoute
            isAuthenticated={isAuthenticated()}
            userRole={getUserRole()}
          >
            <BlogManagement />
          </AdminRoute>
        }
      />

      <Route
        path="/admin-profile"
        element={
          <AdminRoute
            isAuthenticated={isAuthenticated()}
            userRole={getUserRole()}
          >
            <AdminProfile />
          </AdminRoute>
        }
      />

      <Route
        path="/register-admin"
        element={
          <AdminRoute
            isAuthenticated={isAuthenticated()}
            userRole={getUserRole()}
          >
            <RegisterAdmin />
          </AdminRoute>
        }
      />
    </Routes>
  );
}

export default App;
