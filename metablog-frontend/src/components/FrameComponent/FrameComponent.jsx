import PropTypes from "prop-types";

const FrameComponent = ({ className = "" }) => {
  return (
    <div className={`relative w-full md:w-1/2 ${className}`}>
      <img className="absolute inset-0 w-full h-full object-cover" src="/background.svg" alt="background" />
      <div className="relative flex flex-col items-start justify-start p-20 h-full">
        <img className="w-40 mb-10" src="/logo.svg" alt="MetaBlog Logo" />
        <h1 className="text-4xl font-bold text-white">Blogs to dive into tech</h1>
      </div>
    </div>
  );
};

FrameComponent.propTypes = {
  className: PropTypes.string,
};

export default FrameComponent;
