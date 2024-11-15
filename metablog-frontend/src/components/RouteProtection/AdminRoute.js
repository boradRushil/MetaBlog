import React from 'react';
import { Navigate } from 'react-router-dom';

const AdminRoute = ({ children, isAuthenticated, userRole }) => {
  if (!isAuthenticated || userRole !== 'Admin') {
    window.alert("You are not authorized to access this page");
    localStorage.clear();
    return <Navigate to="/login" />;
  }

  return children;
};

export default AdminRoute;