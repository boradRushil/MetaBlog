package com.group3.metaBlog.Authentication.Service;

import com.group3.metaBlog.Authentication.DataTransferObject.*;
import com.group3.metaBlog.Config.ApplicationConfig;
import com.group3.metaBlog.Email.Service.IEmailService;
import com.group3.metaBlog.Enum.Role;
import com.group3.metaBlog.Exception.MetaBlogException;
import com.group3.metaBlog.Jwt.ServiceLayer.JwtService;
import com.group3.metaBlog.OTP.Service.IOTPService;
import com.group3.metaBlog.User.Model.User;
import com.group3.metaBlog.User.Repository.IUserRepository;
import com.group3.metaBlog.Utils.MetaBlogResponse;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private static final String TEST_EMAIL = "@example.com";
    private static final String TEST_USERNAME = "User";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_NEW_PASSWORD = "newPassword";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String USER_ROLE = "User";
    private static final String ADMIN_ROLE = "Admin";
    private static final long USER_ID = 1L;
    private static final int OTP = 123456;
    
    @Mock
    private IUserRepository userRepository;

    @Mock
    private IOTPService otpService;

    @Mock
    private IEmailService emailService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private RegisterRequestDto registerRequestDto;
    private ResetPasswordRequestDto resetPasswordRequestDto;
    private LoginRequestDto loginRequestDto;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(userRepository, otpService, emailService, jwtService, applicationConfig, authenticationManager);

        user = User.builder()
                .id(USER_ID)
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .role(Role.User)
                .build();
        registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername(TEST_USERNAME);
        registerRequestDto.setEmail(TEST_EMAIL);
        registerRequestDto.setPassword(TEST_PASSWORD);
        registerRequestDto.setRole(USER_ROLE);

        resetPasswordRequestDto = new ResetPasswordRequestDto();
        resetPasswordRequestDto.setEmail(TEST_EMAIL);
        resetPasswordRequestDto.setNewPassword(TEST_NEW_PASSWORD);

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(TEST_EMAIL);
        loginRequestDto.setPassword(TEST_PASSWORD);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setAccessToken(ACCESS_TOKEN);
        loginResponseDto.setRefreshToken(REFRESH_TOKEN);
    }

    @Test
    void RegisterUserAlreadyExistsTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = authenticationService.register(registerRequestDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assert responseBody != null;
        assertEquals("User already exists with this email.", responseBody.getMessage());
        assertEquals(false, responseBody.getSuccess());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void RegisterInvalidRoleTest() {
        registerRequestDto.setRole(ADMIN_ROLE);

        ResponseEntity<Object> response = authenticationService.register(registerRequestDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assert responseBody != null;
        assertEquals("Invalid Role", responseBody.getMessage());
        assertEquals(false, responseBody.getSuccess());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void RegisterSuccessTest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(otpService.generateOTP()).thenReturn(OTP);
        when(jwtService.generateJwtToken(any())).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(applicationConfig.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<Object> response = authenticationService.register(registerRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assert responseBody != null;
        assertEquals("User Created Successfully", responseBody.getMessage());
        assertEquals(true, responseBody.getSuccess());

        RegisterResponseDto data = (RegisterResponseDto) responseBody.getData();
        assert data != null;
        assertEquals(ACCESS_TOKEN, data.getAccessToken());
        assertEquals(REFRESH_TOKEN, data.getRefreshToken());
        assertEquals(USER_ROLE, data.getRole());

        verify(userRepository).findByEmail(anyString());
        verify(userRepository, times(3)).save(any(User.class)); // 1 for saving user, 2 for saving access and 3 for refresh token
    }
    @Test
    void RegisterEmailExceptionTest() throws MessagingException {
        // Mock behavior for user creation
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(applicationConfig.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(USER_ID);
            return savedUser;
        });
        when(otpService.generateOTP()).thenReturn(OTP);

        doThrow(new MessagingException("Email error")).when(emailService).sendVerificationOTP(anyString(), anyInt());

        ResponseEntity<Object> response = authenticationService.register(registerRequestDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Error sending email to the user.", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());

        InOrder inOrder = inOrder(userRepository, otpService, emailService);
        inOrder.verify(userRepository).findByEmail(TEST_EMAIL);
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(otpService).generateOTP();
        inOrder.verify(otpService).registerOTP(anyInt(), eq(USER_ID)); // Use USER_ID directly for verification
        inOrder.verify(emailService).sendVerificationOTP(anyString(), anyInt());
    }

    @Test
    void RegisterMetaBlogExceptionTest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(otpService.generateOTP()).thenReturn(OTP);
        when(applicationConfig.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        doThrow(new MetaBlogException("MetaBlogException occurred")).when(jwtService).generateJwtToken(any());

        ResponseEntity<Object> response = authenticationService.register(registerRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("MetaBlogException occurred", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());
    }
    @Test
    void ForgetPasswordUserDoesNotExistTest() throws MessagingException {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = authenticationService.forgetPassword(TEST_EMAIL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User does not exist with this email.", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(otpService, never()).generateOTP();
        verify(otpService, never()).registerOTP(anyInt(), anyLong());
        verify(emailService, never()).sendVerificationOTP(anyString(), anyInt());
    }

    @Test
    void ForgetPasswordSuccessTest() throws MessagingException {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(otpService.generateOTP()).thenReturn(OTP);

        ResponseEntity<Object> response = authenticationService.forgetPassword(TEST_EMAIL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("OTP has been sent to your email successfully.", responseBody.getMessage());
        assertTrue(responseBody.getSuccess());

        InOrder inOrder = inOrder(userRepository, otpService, emailService);
        inOrder.verify(userRepository).findByEmail(TEST_EMAIL);
        inOrder.verify(otpService).generateOTP();
        inOrder.verify(otpService).registerOTP(anyInt(), eq(USER_ID));
        inOrder.verify(emailService).sendVerificationOTP(anyString(), anyInt());
    }

    @Test
    void ForgetPasswordEmailExceptionTest() throws MessagingException {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(otpService.generateOTP()).thenReturn(OTP);
        doThrow(new MessagingException("Email error")).when(emailService).sendVerificationOTP(anyString(), anyInt());

        ResponseEntity<Object> response = authenticationService.forgetPassword(TEST_EMAIL);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Error sending email to the user.", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());

        InOrder inOrder = inOrder(userRepository, otpService, emailService);
        inOrder.verify(userRepository).findByEmail(TEST_EMAIL);
        inOrder.verify(otpService).generateOTP();
        inOrder.verify(otpService).registerOTP(anyInt(), eq(USER_ID));
        inOrder.verify(emailService).sendVerificationOTP(anyString(), anyInt());
    }

    @Test
    void ForgetPasswordMetaBlogExceptionTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(otpService.generateOTP()).thenReturn(OTP);
        doThrow(new MetaBlogException("MetaBlogException occurred")).when(otpService).registerOTP(anyInt(), anyLong());

        ResponseEntity<Object> response = authenticationService.forgetPassword(TEST_EMAIL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("MetaBlogException occurred", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());
    }

    @Test
    void ResetPasswordUserNotFoundTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = authenticationService.resetPassword(resetPasswordRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User not found.", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void ResetPasswordSuccessTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(applicationConfig.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());

        ResponseEntity<Object> response = authenticationService.resetPassword(resetPasswordRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Password reset successfully.", responseBody.getMessage());
        assertTrue(responseBody.getSuccess());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void ResetPasswordMetaBlogExceptionTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(applicationConfig.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());
        doThrow(new MetaBlogException("MetaBlogException occurred")).when(userRepository).save(any(User.class));

        ResponseEntity<Object> response = authenticationService.resetPassword(resetPasswordRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("MetaBlogException occurred", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());
    }

    @Test
    void FindUserNotFoundTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = authenticationService.findUser(TEST_EMAIL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assert responseBody != null;
        assertEquals("User does not exist with this email.", responseBody.getMessage());
        assertEquals(false, responseBody.getSuccess());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }
    @Test
    void FindUserSuccessTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = authenticationService.findUser(TEST_EMAIL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assert responseBody != null;
        assertEquals("A user with this email exists.", responseBody.getMessage());
        assertEquals(true, responseBody.getSuccess());

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    void FindUserMetaBlogExceptionTest() {
        doThrow(new MetaBlogException("MetaBlogException occurred")).when(userRepository).findByEmail(anyString());

        ResponseEntity<Object> response = authenticationService.findUser(TEST_EMAIL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("MetaBlogException occurred", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());
    }

    @Test
    void loginSuccessTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateJwtToken(user)).thenReturn(user.getAccessToken());
        when(jwtService.generateRefreshToken(user)).thenReturn(user.getRefreshToken());

        ResponseEntity<Object> response = authenticationService.login(loginRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        MetaBlogResponse metaBlogResponse = (MetaBlogResponse) response.getBody();
        assert metaBlogResponse != null;
        assertTrue(metaBlogResponse.getSuccess());
        assertEquals("Login successful", metaBlogResponse.getMessage());
        LoginResponseDto data = (LoginResponseDto) metaBlogResponse.getData();
        assertEquals(user.getAccessToken(), data.getAccessToken());
        assertEquals(user.getRefreshToken(), data.getRefreshToken());
        assertEquals(user.getRole().name(), data.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void loginUserNotFoundTest() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = authenticationService.login(loginRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse metaBlogResponse = (MetaBlogResponse) response.getBody();
        assert metaBlogResponse != null;
        assertFalse(metaBlogResponse.getSuccess());
        assertEquals("User not found", metaBlogResponse.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginMetaBlogExceptionTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        doThrow(new MetaBlogException("MetaBlogException occurred")).when(authenticationManager).authenticate(any());

        ResponseEntity<Object> response = authenticationService.login(loginRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse metaBlogResponse = (MetaBlogResponse) response.getBody();
        assertNotNull(metaBlogResponse);
        assertFalse(metaBlogResponse.getSuccess());
        assertEquals("MetaBlogException occurred", metaBlogResponse.getMessage());
    }
}
