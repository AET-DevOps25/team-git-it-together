package com.gitittogether.skillForge.server.course.dto.request.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotBlank(message = "User ID is required")
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("conversation_id")
    private String conversationId;
    
    @JsonProperty("context_window")
    private Integer contextWindow;
    
    @JsonProperty("system_prompt")
    private String systemPrompt;
} 