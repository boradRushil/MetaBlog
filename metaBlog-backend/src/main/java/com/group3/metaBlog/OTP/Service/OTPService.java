package com.group3.metaBlog.OTP.Service;

import com.group3.metaBlog.OTP.Model.OTPModel;
import com.group3.metaBlog.OTP.Repository.IOTPRepository;
import com.group3.metaBlog.User.Model.User;
import com.group3.metaBlog.User.Repository.IUserRepository;
import com.group3.metaBlog.Utils.MetaBlogResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;

@Service
public class OTPService implements IOTPService {

    private final IOTPRepository otpRepository;
    private final IUserRepository IUserRepository;

    public OTPService(IOTPRepository otpRepository, IUserRepository IUserRepository) {
        this.otpRepository = otpRepository;
        this.IUserRepository = IUserRepository;
    }

    public SecureRandom getSecureRandom() {
        return new SecureRandom();
    }

    @Override
    public Integer generateOTP() {
        SecureRandom random = getSecureRandom();
        return 100000 + random.nextInt(900000);
    }

    @Override
    public boolean registerOTP(Integer otp_code, Long userId) {
        OTPModel otpModel = otpRepository.findByUserId(userId);
        if (otpModel != null) {
            otpModel.setOtp(otp_code);
            otpModel.setCreatedAt(System.currentTimeMillis());
            otpModel.setExpiryTime(System.currentTimeMillis() + 600000 * 3); // 30 minutes
            otpRepository.save(otpModel);
        } else {
            OTPModel otp = OTPModel.builder()
                    .otp(otp_code)
                    .userId(userId)
                    .createdAt(System.currentTimeMillis())
                    .expiryTime(System.currentTimeMillis() + 600000 * 3) // 30 minutes
                    .build();
            otpRepository.save(otp);
        }
        return true;
    }

    @Override
    public ResponseEntity<Object> verifyOTP(Integer otp_code, String Email) {
        try {
            Optional<User> user = IUserRepository.findByEmail(Email);
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                        .success(false)
                        .message("User not found")
                        .build());
            } else {
                OTPModel otpModel = otpRepository.findByUserId(user.get().getId());
                if (otpModel == null) {
                    return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                            .success(false)
                            .message("OTP not found")
                            .build());
                }
                if (Objects.equals(otpModel.getOtp(), otp_code)) {
                    if (otpModel.getExpiryTime() > System.currentTimeMillis()) {
                        otpModel.setExpiryTime(System.currentTimeMillis() - 1);
                        otpRepository.save(otpModel);
                        user.get().setIsEmailVerified(true);
                        IUserRepository.save(user.get());

                        return ResponseEntity.ok(MetaBlogResponse.builder()
                                .success(true)
                                .message("OTP Verified successfully")
                                .data(user.get().getUsername())
                                .build());
                    } else {
                        return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                                .success(false)
                                .message("OTP Expired")
                                .build());
                    }
                } else {
                    return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                            .success(false)
                            .message("OTP Does not match")
                            .build());
                }
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message("OTP Verification Failed with error:" + e.getMessage())
                    .build());
        }
    }
}
