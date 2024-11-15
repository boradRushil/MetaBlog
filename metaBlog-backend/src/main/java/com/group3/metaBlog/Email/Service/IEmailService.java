package com.group3.metaBlog.Email.Service;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendVerificationOTP(String emailAddress, Integer otp) throws MessagingException;
}
