import React from "react";
import { useLocation, useParams } from "react-router-dom";
import Header from "../../components/Header/Header";
import Footer from "../../components/Footer/Footer";
import BlogCard from "../../components/BlogCard/BlogCard";

const SearchResult = () => {
  const location = useLocation();
  const {searchTerm}=useParams()
  const searchResults = location.state?.searchResults || [];
  console.log("kavan",searchResults)
  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <main className="flex-1 p-4 md:p-10">
        <div className="container mx-auto">
          {searchResults.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 mt-8">
              {searchResults.map((blog) => (
                <BlogCard key={blog.id} blog={blog} />
              ))}
            </div>
          ) : (
            <p className="text-center">No results found.</p>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default SearchResult;