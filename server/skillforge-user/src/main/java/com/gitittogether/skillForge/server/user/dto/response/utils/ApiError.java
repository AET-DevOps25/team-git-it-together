package com.gitittogether.skillForge.server.user.dto.response.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON
public class ApiError {
    @Builder.Default
    private Instant timestamp = Instant.now(); // Timestamp of the error occurrence
    @NotBlank
    private int status;           // HTTP status code
    @NotBlank
    private String error;         // HTTP error reason
    @NotBlank
    private String message;       // Detailed error message
    private String path;          // Request path
    private List<String> details; // For validation errors, field-specific messages
}