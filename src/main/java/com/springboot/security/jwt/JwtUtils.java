package com.springboot.security.jwt;

import java.util.Date;

import com.springboot.entities.ResetToken;
import com.springboot.repository.ResetTokenRepository;
import com.springboot.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;

    @Value("${bezkoder.app.jwtExpirationMs}")
    private Integer jwtExpirationMs;

    @Value("${bezkoder.app.jwtExpChangePass}")
    private Integer jwtExpChangePass;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // Lấy token để đổi password
    public String generateJwtTokenToChangePassword(String email) {
        String jwtToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpChangePass))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // Lưu trạng thái token đã tạo vào cơ sở dữ liệu
        ResetToken resetToken = new ResetToken(jwtToken, false);
        resetTokenRepository.save(resetToken);
        return jwtToken;
    }

    // Lấy token đăng nhập
    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setHeaderParam("alg", "typ")
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token");
        }
    }

    // Lấy ra username từ token
    public String getSubjectFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}

