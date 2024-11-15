import PropTypes from "prop-types";
import styles from "./Options.module.css";

const Options = ({ className = "" }) => {
  return (
    <div className={[styles.options, className].join(" ")}>
      <div className={styles.didntReceiveThe}>
        Didn’t receive the email? Check spam or promotion folder or
      </div>
      <button className={styles.resend}>
        <div className={styles.resendEmail}>Resend Email</div>
      </button>
      <button className={styles.login}>
        <div className={styles.backToLogin}>Proceed</div>
      </button>
    </div>
  );
};

Options.propTypes = {
  className: PropTypes.string,
};

export default Options;
