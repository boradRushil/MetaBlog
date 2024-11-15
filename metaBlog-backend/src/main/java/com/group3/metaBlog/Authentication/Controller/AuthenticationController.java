package com.group3.metaBlog.Authentication.Controller;

import com.group3.metaBlog.Authentication.DataTransferObject.LoginRequestDto;
import com.group3.metaBlog.Authentication.DataTransferObject.RegisterRequestDto;
import com.group3.metaBlog.Authentication.DataTransferObject.ResetPasswordRequestDto;
import com.group3.metaBlog.Authentication.Service.IAuthenticationService;
import com.group3.metaBlog.Exception.MetaBlogException;
import com.group3.metaBlog.Utils.MetaBlogResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(IAuthenticationService service) {
        this.authenticationService = service;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@NotNull @RequestBody RegisterRequestDto request) {
        try {
            return authenticationService.register(request);
        } catch (MetaBlogException e) {
            logger.error("Error registering user with email: {}", request.getEmail());
            logger.error("Message of the error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/forget-password")
    public ResponseEntity<Object> forgetPassword(@NotNull @RequestParam String email) {
        try {
            return authenticationService.forgetPassword(email);
        } catch (MetaBlogException e) {
            logger.error("Error forgetting password for user with email: {}", email);
            logger.error("Message of the error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@NotNull @RequestBody ResetPasswordRequestDto request) {
        try {
            return authenticationService.resetPassword(request);
        } catch (MetaBlogException e) {
            logger.error("Error resetting password for user with email: {}", request.getEmail());
            logger.error("Message of the error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/get-user")
    public ResponseEntity<Object> getUser(@NotNull @RequestParam String email) {
        try {
            if (email.isEmpty()) {
                return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                        .success(false)
                        .message("Email is empty")
                        .build());
            }
            return authenticationService.findUser(email);
        } catch (MetaBlogException e) {
            logger.error("Error getting user with email: {}", email);
            logger.error("Message of the error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@NotNull @RequestBody LoginRequestDto request) {
        try {
            return authenticationService.login(request);
        } catch (MetaBlogException e) {
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
}
