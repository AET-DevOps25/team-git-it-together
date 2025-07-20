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
public class ConversationInfo {
    
    private String conversationId;
    private String userId;
    private String name;
    private Integer messageCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private Integer contextWindow;
} 