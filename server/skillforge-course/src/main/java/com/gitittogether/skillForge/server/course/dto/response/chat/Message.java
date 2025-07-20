package com.gitittogether.skillForge.server.course.dto.response.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    private MessageRole role;
    private String content;
    private LocalDateTime timestamp;
    
    public enum MessageRole {
        SYSTEM, USER, ASSISTANT;
        
        @JsonValue
        public String toValue() {
            return this.name().toLowerCase();
        }
        
        @JsonCreator
        public static MessageRole fromValue(String value) {
            if (value == null) {
                return null;
            }
            try {
                return MessageRole.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle case-insensitive matching and map lowercase to uppercase
                String upperValue = value.toUpperCase();
                switch (upperValue) {
                    case "USER":
                        return MessageRole.USER;
                    case "ASSISTANT":
                        return MessageRole.ASSISTANT;
                    case "SYSTEM":
                        return MessageRole.SYSTEM;
                    default:
                        throw e;
                }
            }
        }
    }
} 