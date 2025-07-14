package com.gitittogether.skillforge.server.gateway;

import com.gitittogether.skillforge.server.gateway.config.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private static final String TEST_SECRET = "test-jwt-secret-key-for-testing-only-must-be-long-enough";
    private static final long TEST_EXPIRATION = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", TEST_EXPIRATION);
    }

    @Test
    void shouldCreateValidToken() {
        // Given
        String subject = "test-user";
        Date expiration = new Date(System.currentTimeMillis() + TEST_EXPIRATION);

        // When
        String token = createTestToken(subject, expiration);

        // Then
        assertThat(token).isNotNull();
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    void shouldExtractAllClaims() {
        // Given
        String subject = "test-user";
        String token = createTestToken(subject, new Date(System.currentTimeMillis() + TEST_EXPIRATION));

        // When
        Claims claims = jwtUtil.extractAllClaims(token);

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(subject);
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    void shouldExtractSpecificClaim() {
        // Given
        String subject = "test-user";
        String token = createTestToken(subject, new Date(System.currentTimeMillis() + TEST_EXPIRATION));
        Function<Claims, String> subjectResolver = Claims::getSubject;

        // When
        String extractedSubject = jwtUtil.extractClaim(token, subjectResolver);

        // Then
        assertThat(extractedSubject).isEqualTo(subject);
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        // Given
        String subject = "test-user";
        Date pastExpiration = new Date(System.currentTimeMillis() - 1000); // 1 second ago
        String token = createTestToken(subject, pastExpiration);

        // When
        boolean isValid = jwtUtil.isTokenValid(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtil.isTokenValid(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldThrowExceptionForInvalidTokenWhenExtractingClaims() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThatThrownBy(() -> jwtUtil.extractAllClaims(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldHandleNullToken() {
        // Given
        String nullToken = null;

        // When & Then
        assertThatThrownBy(() -> jwtUtil.isTokenValid(nullToken))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldHandleEmptyToken() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtUtil.isTokenValid(emptyToken);

        // Then
        assertThat(isValid).isFalse();
    }

    private String createTestToken(String subject, Date expiration) {
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(subject)
                .expiration(expiration)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }
} 