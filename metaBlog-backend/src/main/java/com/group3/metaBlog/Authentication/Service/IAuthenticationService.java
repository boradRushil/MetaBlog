package com.group3.metaBlog.Authentication.Service;

import com.group3.metaBlog.Authentication.DataTransferObject.*;
import org.springframework.http.ResponseEntity;

public interface IAuthenticationService {
    ResponseEntity<Object> register(RegisterRequestDto request);
    ResponseEntity<Object> forgetPassword(String email);
    ResponseEntity<Object> resetPassword(ResetPasswordRequestDto request);
    ResponseEntity<Object> findUser(String email);
    ResponseEntity<Object> login(LoginRequestDto request);
}
