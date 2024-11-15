package com.group3.metaBlog.OTP.Service;

import com.group3.metaBlog.OTP.Model.OTPModel;
import org.springframework.http.ResponseEntity;

public interface IOTPService {
    Integer generateOTP();
    boolean registerOTP(Integer otp_code, Long userId);
    ResponseEntity<Object> verifyOTP(Integer otp_code, String Email);
}
