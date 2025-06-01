package com.gitittogether.skillForge.server.dto.response.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private int status;           // HTTP status code
    private String error;         // HTTP error reason
    private String message;       // Detailed error message
    private String path;          // Request path
    private List<String> details; // For validation errors, field-specific messages
}