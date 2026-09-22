package com.workchatia.message.domain;

import java.time.Instant;
import java.util.UUID;

public class Message {

    private final UUID id;
    private final UUID conversationId;
    private final UUID authorId;
    private String content;
    private MessageStatus status;
    private final MessageType type;
    private final Instant createdAt;
    private Instant updatedAt;

    private Message(
            UUID id,
            UUID conversationId,
            UUID authorId,
            String content,
            MessageType type,
            MessageStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.authorId = authorId;
        this.content = content;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Message create(
            UUID id,
            UUID conversationId,
            UUID authorId,
            String content,
            MessageType type,
            Instant now) {
        validateContent(content);
        if (conversationId == null || authorId == null || type == null) {
            throw new IllegalArgumentException("conversationId, authorId and type are required");
        }
        return new Message(
                id, conversationId, authorId, content.trim(), type, MessageStatus.DRAFT, now, now);
    }

    public static Message restore(
            UUID id,
            UUID conversationId,
            UUID authorId,
            String content,
            MessageType type,
            MessageStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new Message(id, conversationId, authorId, content, type, status, createdAt, updatedAt);
    }

    public void edit(String newContent, Instant now) {
        validateEditableState();
        validateContent(newContent);
        content = newContent.trim();
        updatedAt = now;
    }

    public void submitForReview(Instant now) {
        require(MessageStatus.DRAFT);
        status = MessageStatus.PENDING_REVIEW;
        updatedAt = now;
    }

    public UUID id() {
        return id;
    }

    public UUID conversationId() {
        return conversationId;
    }

    public UUID authorId() {
        return authorId;
    }

    public String content() {
        return content;
    }

    public MessageStatus status() {
        return status;
    }

    public MessageType type() {
        return type;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    private void validateEditableState() {
        if (status != MessageStatus.DRAFT) {
            throw new IllegalStateException("message cannot be edited");
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content is required");
        }
    }

    private void require(MessageStatus expected) {
        if (status != expected) {
            throw new IllegalStateException("invalid message state: " + status);
        }
    }
}
