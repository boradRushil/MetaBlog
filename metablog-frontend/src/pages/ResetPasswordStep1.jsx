import { useNavigate } from "react-router-dom";
import PropTypes from "prop-types";

const ResetPasswordStep1 = ({ className = "" }) => {
  const navigate = useNavigate();

  const handleLoginClick = () => {
    navigate("/login");
  };

  return (
    <div className={`flex flex-col md:flex-row w-full h-screen ${className}`}>
      <div className="relative w-full md:w-1/2">
        <img className="absolute inset-0 w-full h-full object-cover" src="/background.svg" alt="background" />
        <div className="relative flex flex-col items-start justify-start p-20 h-full">
          <img className="w-40 mb-10" src="/logo.svg" alt="MetaBlog Logo" />
          <h1 className="text-4xl font-bold text-white">Blogs to dive into tech</h1>
        </div>
      </div>
      <div className="flex flex-col items-center justify-center w-full md:w-1/2 p-8 md:p-20 bg-white">
        <div className="w-full max-w-md">
          <div className="bg-green-100 text-green-700 p-6 rounded-lg shadow-md text-center">
            <div className="flex justify-center items-center mb-4">
              <svg className="w-16 h-16 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11.293l3.293 3.293a1 1 0 01-1.414 1.414L10 9.414l-1.293 1.293a1 1 0 01-1.414-1.414l2-2a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
            </div>
            <h2 className="text-3xl font-bold mb-4 text-gray-900">Password reset successfully</h2>
            <button
              type="button"
              className="mt-4 w-full py-4 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700 transition text-base"
              onClick={handleLoginClick}
            >
              Login
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

ResetPasswordStep1.propTypes = {
  className: PropTypes.string,
};

export default ResetPasswordStep1;
