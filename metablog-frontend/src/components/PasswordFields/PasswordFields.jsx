import PropTypes from "prop-types";

const PasswordFields = ({ className = "" }) => {
  return (
    <form className={`space-y-4 ${className}`}>
      <div className="space-y-4">
        <div className="relative">
          <input
            className="w-full p-3 border border-gray-300 rounded"
            placeholder="Your new password"
            type="password"
          />
          <img
            className="absolute top-3 right-3 w-5 h-5 cursor-pointer"
            alt="toggle visibility"
            src="/iconoutlineeyeoff.svg"
          />
        </div>
        <div className="relative">
          <input
            className="w-full p-3 border border-gray-300 rounded"
            placeholder="Confirm your new password"
            type="password"
          />
          <img
            className="absolute top-3 right-3 w-5 h-5 cursor-pointer"
            alt="toggle visibility"
            src="/iconoutlineeyeoff.svg"
          />
        </div>
      </div>
      <button className="w-full py-3 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700 transition">
        Reset Password
      </button>
      <button className="mt-4 w-full py-3 rounded border border-blue-600 text-blue-600 hover:bg-blue-50 transition">
        Back to Login
      </button>
    </form>
  );
};

PasswordFields.propTypes = {
  className: PropTypes.string,
};

export default PasswordFields;
