import PropTypes from "prop-types";
import styles from "./EmailSend.module.css";

const EmailSend = ({ className = "" }) => {
  return (
    <div className={[styles.emailSend, className].join(" ")}>
      <div className={styles.inputButton}>
        <input className={styles.email} placeholder="Email" type="email" />
      </div>
      <button className={styles.inputButton1}>
        <div className={styles.send}>Send</div>
      </button>
      <button className={styles.loginLink}>
        <div className={styles.backToLogin}>Back to Login</div>
      </button>
    </div>
  );
};

EmailSend.propTypes = {
  className: PropTypes.string,
};

export default EmailSend;
