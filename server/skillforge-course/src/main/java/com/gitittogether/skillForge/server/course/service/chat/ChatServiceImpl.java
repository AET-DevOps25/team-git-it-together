package com.gitittogether.skillForge.server.course.service.chat;

import com.gitittogether.skillForge.server.course.dto.request.chat.ChatRequest;
import com.gitittogether.skillForge.server.course.dto.response.chat.ChatResponse;
import com.gitittogether.skillForge.server.course.dto.response.chat.ConversationInfo;
import com.gitittogether.skillForge.server.course.dto.response.chat.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    
    private final RestTemplate restTemplate;
    
    @Value("${genai.service.uri:http://localhost:8888}")
    private String genaiServiceUrl;
    
    @Override
    public ChatResponse sendChatMessage(ChatRequest request) {
        try {
            log.info("Sending chat message for user: {}", request.getUserId());
            
            String url = genaiServiceUrl + "/api/v1/chat";
            ResponseEntity<ChatResponse> response = restTemplate.postForEntity(url, request, ChatResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Chat message sent successfully for user: {}", request.getUserId());
                return response.getBody();
            } else {
                log.error("Failed to send chat message for user: {}", request.getUserId());
                throw new RuntimeException("Failed to send chat message");
            }
        } catch (Exception e) {
            log.error("Error sending chat message for user {}: {}", request.getUserId(), e.getMessage(), e);
            throw new RuntimeException("Error communicating with AI service", e);
        }
    }
    
    @Override
    public List<ConversationInfo> getUserConversations(String userId) {
        try {
            log.info("Fetching conversations for user: {}", userId);
            
            String url = genaiServiceUrl + "/api/v1/chat/conversations/" + userId;
            ResponseEntity<ConversationInfo[]> response = restTemplate.getForEntity(url, ConversationInfo[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<ConversationInfo> conversations = Arrays.asList(response.getBody());
                
                log.info("Retrieved {} conversations for user: {}", conversations.size(), userId);
                return conversations;
            } else {
                log.error("Failed to fetch conversations for user: {}", userId);
                throw new RuntimeException("Failed to fetch conversations");
            }
        } catch (Exception e) {
            log.error("Error fetching conversations for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error communicating with AI service", e);
        }
    }
    
    @Override
    public List<Message> getConversationHistory(String conversationId, String userId) {
        try {
            log.info("Fetching conversation history for conversation: {} and user: {}", conversationId, userId);
            
            String url = genaiServiceUrl + "/api/v1/chat/history/" + conversationId + "/" + userId;
            ResponseEntity<Message[]> response = restTemplate.getForEntity(url, Message[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Message> messages = Arrays.asList(response.getBody());
                
                log.info("Retrieved {} messages for conversation: {}", messages.size(), conversationId);
                return messages;
            } else {
                log.error("Failed to fetch conversation history for conversation: {}", conversationId);
                throw new RuntimeException("Failed to fetch conversation history");
            }
        } catch (Exception e) {
            log.error("Error fetching conversation history for conversation {}: {}", conversationId, e.getMessage(), e);
            throw new RuntimeException("Error communicating with AI service", e);
        }
    }
    
    @Override
    public boolean deleteConversation(String conversationId, String userId) {
        try {
            log.info("Deleting conversation: {} for user: {}", conversationId, userId);
            
            String url = genaiServiceUrl + "/api/v1/chat/conversation/" + conversationId + "/" + userId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully deleted conversation: {} for user: {}", conversationId, userId);
                return true;
            } else {
                log.error("Failed to delete conversation: {} for user: {}", conversationId, userId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting conversation {} for user {}: {}", conversationId, userId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean clearUserConversations(String userId) {
        try {
            log.info("Clearing all conversations for user: {}", userId);
            
            String url = genaiServiceUrl + "/api/v1/chat/conversations/" + userId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully cleared all conversations for user: {}", userId);
                return true;
            } else {
                log.error("Failed to clear conversations for user: {}", userId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error clearing conversations for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean renameConversation(String conversationId, String userId, String newName) {
        try {
            log.info("Renaming conversation: {} for user: {} to: {}", conversationId, userId, newName);
            
            String url = genaiServiceUrl + "/api/v1/chat/conversation/" + conversationId + "/" + userId + "/rename";
            
            // Create request body
            Map<String, String> requestBody = Map.of("new_name", newName);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully renamed conversation: {} for user: {}", conversationId, userId);
                return true;
            } else {
                log.error("Failed to rename conversation: {} for user: {}", conversationId, userId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error renaming conversation {} for user {}: {}", conversationId, userId, e.getMessage(), e);
            return false;
        }
    }
    
    private ConversationInfo mapToConversationInfo(Map<String, Object> data) {
        return ConversationInfo.builder()
            .conversationId((String) data.get("conversation_id"))
            .userId((String) data.get("user_id"))
            .messageCount((Integer) data.get("message_count"))
            .createdAt(parseDateTime((String) data.get("created_at")))
            .lastUpdated(parseDateTime((String) data.get("last_updated")))
            .contextWindow((Integer) data.get("context_window"))
            .build();
    }
    
    private Message mapToMessage(Map<String, Object> data) {
        return Message.builder()
            .role(Message.MessageRole.valueOf(((String) data.get("role")).toUpperCase()))
            .content((String) data.get("content"))
            .timestamp(parseDateTime((String) data.get("timestamp")))
            .build();
    }
    
    private java.time.LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) {
            return java.time.LocalDateTime.now();
        }
        try {
            return java.time.LocalDateTime.parse(dateTimeStr.replace("Z", ""));
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}, using current time", dateTimeStr);
            return java.time.LocalDateTime.now();
        }
    }
} 