package com.group3.metaBlog.OTP.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.security.SecureRandom;
import java.util.Optional;

import com.group3.metaBlog.OTP.Model.OTPModel;
import com.group3.metaBlog.OTP.Repository.IOTPRepository;
import com.group3.metaBlog.User.Model.User;
import com.group3.metaBlog.User.Repository.IUserRepository;
import com.group3.metaBlog.Utils.MetaBlogResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class OTPServiceTest {

    @Mock
    private IOTPRepository otpRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private OTPService otpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void GenerateOTPTest() {
        SecureRandom random = mock(SecureRandom.class);
        when(random.nextInt(anyInt())).thenReturn(123456);

        OTPService otpServiceSpy = spy(otpService);
        doReturn(random).when(otpServiceSpy).getSecureRandom();

        int otp = otpServiceSpy.generateOTP();
        assertEquals(223456, otp);
    }
    @Test
    void SecureRandomTest() {
        SecureRandom secureRandom = otpService.getSecureRandom();
        assertNotNull(secureRandom);
    }

    @Test
    void RegisterOTP_NewOTPTest() {
        Long userId = 1L;
        int otpCode = 123456;

        when(otpRepository.findByUserId(userId)).thenReturn(null);

        boolean result = otpService.registerOTP(otpCode, userId);

        assertTrue(result);
        verify(otpRepository, times(1)).save(any(OTPModel.class));
    }

    @Test
    void RegisterOTP_UpdateExistingOTPTest() {
        Long userId = 1L;
        int otpCode = 123456;
        OTPModel existingOtp = new OTPModel();

        when(otpRepository.findByUserId(userId)).thenReturn(existingOtp);

        boolean result = otpService.registerOTP(otpCode, userId);

        assertTrue(result);
        verify(otpRepository, times(1)).save(existingOtp);
    }

    @Test
    void VerifyOTP_UserNotFoundTest() {
        String email = "test@example.com";
        int otpCode = 123456;

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = otpService.verifyOTP(otpCode, email);

        assertEquals(ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                .success(false)
                .message("User not found")
                .build()), response);
    }

    @Test
    void VerifyOTP_OTPNotFoundTest() {
        String email = "test@example.com";
        int otpCode = 123456;
        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserId(user.getId())).thenReturn(null);

        ResponseEntity<Object> response = otpService.verifyOTP(otpCode, email);

        assertEquals(ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                .success(false)
                .message("OTP not found")
                .build()), response);
    }

    @Test
    void VerifyOTP_OTPMatchesAndNotExpiredTest() {
        String email = "test@example.com";
        int otpCode = 123456;
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        OTPModel otpModel = new OTPModel();
        otpModel.setOtp(otpCode);
        otpModel.setExpiryTime(System.currentTimeMillis() + 600000);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserId(user.getId())).thenReturn(otpModel);

        ResponseEntity<Object> response = otpService.verifyOTP(otpCode, email);

        assertEquals(ResponseEntity.ok(MetaBlogResponse.builder()
                .success(true)
                .message("OTP Verified successfully")
                .data(user.getUsername())
                .build()), response);
    }

    @Test
    void VerifyOTP_OTPMatchesButExpiredTest() {
        String email = "test@example.com";
        int otpCode = 123456;
        User user = new User();
        user.setId(1L);

        OTPModel otpModel = new OTPModel();
        otpModel.setOtp(otpCode);
        otpModel.setExpiryTime(System.currentTimeMillis() - 1000);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserId(user.getId())).thenReturn(otpModel);

        ResponseEntity<Object> response = otpService.verifyOTP(otpCode, email);

        assertEquals(ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                .success(false)
                .message("OTP Expired")
                .build()), response);
    }

    @Test
    void VerifyOTP_OTPDoesNotMatchTest() {
        String email = "test@example.com";
        int otpCode = 123456;
        User user = new User();
        user.setId(1L);

        OTPModel otpModel = new OTPModel();
        otpModel.setOtp(654321);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserId(user.getId())).thenReturn(otpModel);

        ResponseEntity<Object> response = otpService.verifyOTP(otpCode, email);

        assertEquals(ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                .success(false)
                .message("OTP Does not match")
                .build()), response);
    }

    @Test
    void VerifyOTP_IllegalArgumentExceptionTest() {
        String email = "test@example.com";
        int otpCode = 123456;

        when(userRepository.findByEmail(email)).thenThrow(new IllegalArgumentException("Invalid argument"));

        ResponseEntity<Object> response = otpService.verifyOTP(otpCode, email);

        assertEquals(ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                .success(false)
                .message("OTP Verification Failed with error:Invalid argument")
                .build()), response);
    }
}
