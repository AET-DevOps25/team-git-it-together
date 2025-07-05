package com.gitittogether.skillForge.server.user.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;

/**
 * This filter:
 * 1) Checks for the presence of a JWT in the Authorization header.
 * 2) Validates the JWT.
 * 3) If valid, retrieves user details and sets the authentication in the SecurityContext.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final JwtUserDetailsService userDetailsService;
    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    /**
     * This filter runs for every request to check if the user is authenticated via JWT.
     * It extracts the JWT from the Authorization header, validates it, and sets the authentication in the SecurityContext.
     *
     * @param request     The HTTP request
     * @param response    The HTTP response
     * @param filterChain The filter chain to continue processing
     * @throws ServletException If an error occurs during filtering
     * @throws IOException      If an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String jwt = null;
            String userId = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                userId = jwtUtils.extractUserId(jwt);
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtils.isTokenValid(jwt, userId)) {
                    UserDetails userDetails = userDetailsService.loadUserById(userId);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    throw new BadCredentialsException("JWT token is invalid");
                }
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            log.warn("Authentication failure: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
            jwtAuthEntryPoint.commence(request, response, ex);
        }
    }

}