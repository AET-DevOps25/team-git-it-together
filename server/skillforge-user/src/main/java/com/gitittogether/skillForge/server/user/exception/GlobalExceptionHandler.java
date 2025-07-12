package com.gitittogether.skillForge.server.user.exception;

import com.gitittogether.skillForge.server.user.dto.response.utils.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("🚫 [BadRequest] {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildError(HttpStatus.BAD_REQUEST, "🚫 Bad Request", ex, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("🔎 [ResourceNotFound] {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildError(HttpStatus.NOT_FOUND, "🔎 Not Found", ex, request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest req) {
        log.warn("🔑 [UsernameNotFound] {} - Path: {}", ex.getMessage(), req.getRequestURI());
        // Use 401 for auth, 404 for profile lookups if you want
        HttpStatus status = req.getRequestURI().contains("/login") ? HttpStatus.UNAUTHORIZED : HttpStatus.NOT_FOUND;
        String error = status == HttpStatus.UNAUTHORIZED ? "🔑 Unauthorized" : "🔎 Not Found";
        return buildError(status, error, ex, req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest req) {
        log.warn("🔒 [AuthenticationException] {} - Path: {}", ex.getMessage(), req.getRequestURI());
        return buildError(HttpStatus.UNAUTHORIZED, "🔒 Unauthorized", ex, req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        log.warn("🚫 [AccessDenied] {} - Path: {}", ex.getMessage(), req.getRequestURI());
        return buildError(HttpStatus.FORBIDDEN, "🚫 Forbidden", ex, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());
        ApiError error = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("🚫 Validation Error")
                .message(ex.getMessage())
                .details(details)
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ApiError> handleWrongPassword(WrongPasswordException ex, HttpServletRequest request) {
        log.warn("🔒 [WrongPassword] {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildError(HttpStatus.UNAUTHORIZED, "🔒 Unauthorized", ex, request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("🔎 [NoHandlerFound] {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildError(HttpStatus.NOT_FOUND, "🔎 Not Found", ex, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("🚫 [MethodNotAllowed] {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildError(HttpStatus.METHOD_NOT_ALLOWED, "🚫 Method Not Allowed", ex, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("🚫 [MissingParameter] {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildError(HttpStatus.BAD_REQUEST, "🚫 Missing Parameter", ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaught(Exception ex, HttpServletRequest request) {
        log.error("💥 [InternalError] Unexpected: {} - Path: {}", ex.getMessage(), request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "💥 Internal Server Error " + ex.getClass().getSimpleName(), ex, request);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String error, Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ApiError.builder()
                        .status(status.value())
                        .error(error)
                        .message(ex.getMessage())
                        .path(req.getRequestURI())
                        .timestamp(Instant.now())
                        .build());
    }
}