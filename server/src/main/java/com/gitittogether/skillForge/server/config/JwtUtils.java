package com.gitittogether.skillForge.server.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for a given user.
     *
     * @param userId   The unique identifier of the user for whom the token is being generated.
     * @param username The username of the user, included as a claim in the token.
     * @return A signed JWT token as a String.
     */
    public String generateToken(String userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256) // Modern way!
                .compact();
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token from which to extract claims.
     * @return A Claims object containing all the claims in the token.
     * @throws JwtException if the token is invalid or expired.
     */
    public Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
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
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the user ID (subject) from the JWT token.
     *
     * @param token The JWT token from which to extract the user ID.
     * @return The user ID as a String.
     */
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token The JWT token from which to extract the username.
     * @return The username as a String.
     */
    public boolean isTokenValid(String token, String userId) {
        try {
            final String subject = extractUserId(token);
            final Date expiration = extractClaim(token, Claims::getExpiration);
            return (subject.equals(userId) && !expiration.before(new Date()));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
