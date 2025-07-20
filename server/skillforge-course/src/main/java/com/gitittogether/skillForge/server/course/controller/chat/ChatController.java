package com.gitittogether.skillForge.server.course.controller.chat;

import com.gitittogether.skillForge.server.course.dto.request.chat.ChatRequest;
import com.gitittogether.skillForge.server.course.dto.request.chat.RenameRequest;
import com.gitittogether.skillForge.server.course.dto.response.chat.ChatResponse;
import com.gitittogether.skillForge.server.course.dto.response.chat.ConversationInfo;
import com.gitittogether.skillForge.server.course.dto.response.chat.Message;
import com.gitittogether.skillForge.server.course.service.chat.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    
    private final ChatService chatService;
    
    @PostMapping
    public ResponseEntity<ChatResponse> sendChatMessage(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat message from user: {}", request.getUserId());
        ChatResponse response = chatService.sendChatMessage(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ConversationInfo>> getUserConversations(@PathVariable String userId) {
        log.info("Fetching conversations for user: {}", userId);
        List<ConversationInfo> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }
    
    @GetMapping("/history/{conversationId}/{userId}")
    public ResponseEntity<List<Message>> getConversationHistory(
            @PathVariable String conversationId,
            @PathVariable String userId) {
        log.info("Fetching conversation history for conversation: {} and user: {}", conversationId, userId);
        List<Message> history = chatService.getConversationHistory(conversationId, userId);
        return ResponseEntity.ok(history);
    }
    
    @DeleteMapping("/conversation/{conversationId}/{userId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable String conversationId,
            @PathVariable String userId) {
        log.info("Deleting conversation: {} for user: {}", conversationId, userId);
        boolean success = chatService.deleteConversation(conversationId, userId);
        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/conversations/{userId}")
    public ResponseEntity<Void> clearUserConversations(@PathVariable String userId) {
        log.info("Clearing all conversations for user: {}", userId);
        boolean success = chatService.clearUserConversations(userId);
        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/conversation/{conversationId}/{userId}/rename")
    public ResponseEntity<Void> renameConversation(
            @PathVariable String conversationId,
            @PathVariable String userId,
            @RequestBody RenameRequest request) {
        log.info("Renaming conversation: {} for user: {} to: {}", conversationId, userId, request.getNewName());
        boolean success = chatService.renameConversation(conversationId, userId, request.getNewName());
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 