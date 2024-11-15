package com.group3.metaBlog.Jwt.ServiceLayer;

import com.group3.metaBlog.User.Model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface IJwtService {
    String extractUserEmailFromToken(String jwtToken);
    boolean isTokenValid(String jwt, UserDetails userDetails);
    String generateJwtToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    boolean isTokenExpired(String jwtToken);
    String extractJwtTokenType(String jwtToken);
    String getUserEmailFromToken(String token);
}
