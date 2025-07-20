package com.gitittogether.skillForge.server.course.service.chat;

import com.gitittogether.skillForge.server.course.dto.request.chat.ChatRequest;
import com.gitittogether.skillForge.server.course.dto.response.chat.ChatResponse;
import com.gitittogether.skillForge.server.course.dto.response.chat.ConversationInfo;
import com.gitittogether.skillForge.server.course.dto.response.chat.Message;

import java.util.List;

public interface ChatService {
    
    /**
     * Sends a chat message to the AI service and returns the response.
     *
     * @param request The chat request containing the message and user information.
     * @return The chat response from the AI service.
     */
    ChatResponse sendChatMessage(ChatRequest request);
    
    /**
     * Retrieves all conversations for a specific user.
     *
     * @param userId The ID of the user.
     * @return List of conversation information.
     */
    List<ConversationInfo> getUserConversations(String userId);
    
    /**
     * Retrieves the conversation history for a specific conversation.
     *
     * @param conversationId The ID of the conversation.
     * @param userId The ID of the user.
     * @return List of messages in the conversation.
     */
    List<Message> getConversationHistory(String conversationId, String userId);
    
    /**
     * Deletes a specific conversation for a user.
     *
     * @param conversationId The ID of the conversation to delete.
     * @param userId The ID of the user.
     * @return True if the conversation was deleted successfully.
     */
    boolean deleteConversation(String conversationId, String userId);
    
    /**
     * Clears all conversations for a user.
     *
     * @param userId The ID of the user.
     * @return True if all conversations were cleared successfully.
     */
    boolean clearUserConversations(String userId);
    
    /**
     * Renames a conversation for a user.
     *
     * @param conversationId The ID of the conversation to rename.
     * @param userId The ID of the user.
     * @param newName The new name for the conversation.
     * @return True if the conversation was renamed successfully.
     */
    boolean renameConversation(String conversationId, String userId, String newName);
} 