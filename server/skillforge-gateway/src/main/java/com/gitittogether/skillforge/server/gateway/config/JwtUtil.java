package com.gitittogether.skillforge.server.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Logs JWT secret information for debugging (without exposing the actual secret)
     */
    private void logSecretInfo() {
        if (jwtSecret != null) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(jwtSecret.getBytes(StandardCharsets.UTF_8));
                String secretHash = bytesToHex(hash).substring(0, 16); // First 16 chars of hash
                log.info("JwtUtil: JWT secret loaded - Length: {}, Hash: {}..., Using default: {}", 
                        jwtSecret.length(), secretHash, jwtSecret.equals("default-secret-key-for-development-only-change-in-production"));
            } catch (NoSuchAlgorithmException e) {
                log.warn("JwtUtil: Could not hash JWT secret for logging");
            }
        } else {
            log.error("JwtUtil: JWT secret is null!");
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token from which to extract claims.
     * @return A Claims object containing all the claims in the token.
     * @throws JwtException if the token is invalid or expired.
     */
    public Claims extractAllClaims(String token) throws JwtException {
        log.debug("JwtUtil: Extracting claims from token (first 10 chars): {}", 
                 token.length() > 10 ? token.substring(0, 10) + "..." : token);
        
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            log.debug("JwtUtil: Successfully extracted claims. Subject: {}, Expiration: {}", 
                     claims.getSubject(), claims.getExpiration());
            return claims;
        } catch (JwtException e) {
            log.error("JwtUtil: Failed to extract claims from token: {}", e.getMessage());
            log.error("JwtUtil: This usually indicates a JWT secret mismatch between services");
            throw e;
        }
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver function.
     *
     * @param token          The JWT token from which to extract the claim.
     * @param claimsResolver A function that takes Claims and returns the desired claim.
     * @param <T>            The type of the claim to be extracted.
     * @return The extracted claim of type T.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.debug("JwtUtil: Extracting specific claim from token");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Checks if the JWT token is valid by verifying its expiration date.
     * @param token The JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token) {
        log.debug("JwtUtil: Validating token (first 10 chars): {}", 
                 token.length() > 10 ? token.substring(0, 10) + "..." : token);
        
        try {
            final Date expiration = extractClaim(token, Claims::getExpiration);
            final Date now = new Date();
            boolean isValid = !expiration.before(now);
            
            log.debug("JwtUtil: Token validation result: {}. Expiration: {}, Current time: {}", 
                     isValid, expiration, now);
            
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JwtUtil: Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Post-construction method to log secret information
     */
    public void initialize() {
        logSecretInfo();
    }
}
