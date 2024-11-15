package com.group3.metaBlog.Email.Service;

import com.group3.metaBlog.Exception.MetaBlogException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendVerificationOTP(@NotNull String emailAddress, @NotNull Integer otp) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        try {
            helper.setTo(emailAddress);
            helper.setSubject("Account verification OTP");

            Context context = new Context();
            context.setVariable("otp", otp);

            String htmlContent = templateEngine.process("verifyaccount", context);

            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MetaBlogException("Failed to send reset email: " + e.getMessage());
        }
    }
}
