package com.group3.metaBlog.Jwt.ServiceLayer;

import com.group3.metaBlog.User.Model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.security.StandardSecureDigestAlgorithms;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService implements IJwtService {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Override
    public String extractUserEmailFromToken(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUserEmailFromToken(jwt);
        User currentUser = (User) userDetails;
        return (username.equals(currentUser.getEmail()) && !isTokenExpired(jwt));
    }

    @Override
    public String generateJwtToken(UserDetails userDetails) {
        return generateJwtToken(new HashMap<>(), userDetails);
    }

    private String generateJwtToken(Map<String, Objects> claims, UserDetails userDetails) {
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("isAccountExpired", !userDetails.isAccountNonExpired());
        userClaims.put("id", ((User) userDetails).getId());
        userClaims.put("role", userDetails.getAuthorities().toArray()[0].toString());
        userClaims.put("username", userDetails.getUsername());
        userClaims.put("lastLogin", ((User) userDetails).getLastLoginTime());
        userClaims.put("registerAt", ((User) userDetails).getRegisterAt());
        userClaims.put("type", "access");
        return Jwts.builder()
                .claims(userClaims)
                .subject(((User) userDetails).getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10000 * 60 * 12))
                .signWith(getSigningKey(), StandardSecureDigestAlgorithms.findBySigningKey(getSigningKey()))
                .compact();
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        long refreshExpiration = Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond();
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("type", "refresh");
        return Jwts
                .builder()
                .claims(extraClaims)
                .claims(userClaims)
                .subject(((User) userDetails).getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), StandardSecureDigestAlgorithms.findBySigningKey(getSigningKey()))
                .compact();
    }

    @Override
    public boolean isTokenExpired(String jwtToken) {
        Date jwtExpireTime = extractExpirationOfJwt(jwtToken);
        return jwtExpireTime.before(new Date());
    }

    private Date extractExpirationOfJwt(String jwtToken) {
        return extractClaims(jwtToken, Claims::getExpiration);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    @Override
    public String extractJwtTokenType(String jwtToken) {
        return extractAllClaims(jwtToken).get("type").toString();
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(jwtToken).getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

    @Override
    public String getUserEmailFromToken(String token) {
        return extractUserEmailFromToken(token);
    }
}
