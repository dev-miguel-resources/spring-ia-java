package com.ia.agentsia.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.stereotype.Component;

@Component
public class ChatHistory {

    private static final Logger logger = LoggerFactory.getLogger(ChatHistory.class);

    // Cuaderno con la conversación de mensajes
    private final Map<String, List<Message>> chatHistoryLog;

    // Borrador de mensajes
    private final Map<String, List<Message>> messageAggregations;

    public ChatHistory() {
        this.chatHistoryLog = new ConcurrentHashMap<>();
        this.messageAggregations = new ConcurrentHashMap<>();
    }

    public void addMessage(String chatId, Message message) {

        // chat1:msg1
        String groupId = toGroupId(chatId, message);

        // Revisión de borradores
        this.messageAggregations.computeIfAbsent(groupId, key -> new ArrayList<>()).add(message);

        if (this.messageAggregations.size() > 1) {
            logger.info("Active sessions messages: ", this.messageAggregations.keySet());
        }

        String finishReason = getProperty(message, "finishReason");

        if ("STOP".equalsIgnoreCase(finishReason) || message.getMessageType() == MessageType.USER) {

            this.finalizeMessageGroup(chatId, groupId);
        }

    }

    private String toGroupId(String chatId, Message message) {

        String messageId = getProperty(message, "id");

        // idChat:idMesage
        return chatId + ":" + messageId;
    }

    private String getProperty(Message message, String key) {
        return message.getMetadata().getOrDefault(key, "").toString();
    }

    private void finalizeMessageGroup(String chatId, String groupId) {

        List<Message> sessionMessages = this.messageAggregations.remove(groupId);

        if (sessionMessages != null) {
            if (sessionMessages.size() == 1) {
                this.commitToHistoryLog(chatId, sessionMessages.get(0));
            } else {
                String aggregatedContent = sessionMessages.stream()
                        .map(Message::getText)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining());

                this.commitToHistoryLog(chatId, new AssistantMessage(aggregatedContent));
            }
        }
    }

    private void commitToHistoryLog(String chatId, Message message) {
        this.chatHistoryLog.computeIfAbsent(chatId, key -> new ArrayList<>()).add(message);
    }

    public List<Message> getAll(String chatId) {
        return this.chatHistoryLog.getOrDefault(chatId, List.of());
    }

}
