package com.group3.metaBlog.OTP.Repository;

import com.group3.metaBlog.OTP.Model.OTPModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOTPRepository extends JpaRepository<OTPModel, Integer> {
    OTPModel findByUserId(Long userId);
}
