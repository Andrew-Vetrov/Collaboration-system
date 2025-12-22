package application.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
    public UUID getCurrentUserId() throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()|| "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthException("User not authenticated");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String userIdStr = jwt.getSubject();
            try {
                UUID ret = UUID.fromString(userIdStr);
                log.info("Found user with id " + ret);
                return ret;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID in JWT subject: " + userIdStr);
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass());
        }
    }
}
