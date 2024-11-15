package com.group3.metaBlog.Email.Service;

import com.group3.metaBlog.Exception.MetaBlogException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    public void SendVerificationOTPTest() throws MessagingException {
        String email = "test@example.com";
        int otp = 123456;
        String htmlContent = "<html>OTP: " + otp + "</html>";

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(htmlContent);

        emailService.sendVerificationOTP(email, otp);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);

        verify(templateEngine).process(eq("verifyaccount"), any(Context.class));
    }

    @Test
    public void SendVerificationOTP_ThrowsMetaBlogExceptionTest() {
        String email = "test@example.com";
        int otp = 123456;

        when(javaMailSender.createMimeMessage()).thenThrow(new MetaBlogException("Failed to send reset email: "));
        MetaBlogException exception = assertThrows(MetaBlogException.class, () -> emailService.sendVerificationOTP(email, otp));
        assertEquals("Failed to send reset email: ", exception.getMessage());
    }
}
