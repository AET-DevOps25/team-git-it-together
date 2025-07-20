package com.gitittogether.skillForge.server.course.dto.response.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    private String message;
    private String conversationId;
    private String userId;
    private LocalDateTime timestamp;
    private Integer contextLength;
    private String provider;
} 