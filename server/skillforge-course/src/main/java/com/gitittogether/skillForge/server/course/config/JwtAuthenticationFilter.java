package com.gitittogether.skillForge.server.course.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT authentication filter for the course service.
 * This filter validates JWT tokens and sets authentication context.
 * Since the course service is called only through the API gateway,
 * we don't need to load user details from a database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String jwt = null;
        String userId = null;

        // JWT should be in the form "Bearer eyJ..."
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                userId = jwtUtils.extractUserId(jwt);
            } catch (Exception exception) {
                log.error("Failed to extract user ID from JWT", exception);
            }
        }

        // Validate and set authentication in context
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtils.isTokenValid(jwt, userId)) {
                // Create authentication token with user ID and basic USER authority
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId, 
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority("USER"))
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Authentication set for user: {}", userId);
            } else {
                log.warn("Invalid JWT token for user: {}", userId);
            }
        }

        filterChain.doFilter(request, response);
    }
} 