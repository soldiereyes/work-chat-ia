package com.workchatia.conversation.domain;

import java.time.Instant;
import java.util.UUID;

public class Conversation {

    private final UUID id;
    private final UUID accountId;
    private final UUID professionalId;
    private ConversationStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Conversation(
            UUID id,
            UUID accountId,
            UUID professionalId,
            ConversationStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.professionalId = professionalId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Conversation create(UUID id, UUID accountId, UUID professionalId, Instant now) {
        if (accountId == null || professionalId == null) {
            throw new IllegalArgumentException("accountId and professionalId are required");
        }
        return new Conversation(id, accountId, professionalId, ConversationStatus.OPEN, now, now);
    }

    public static Conversation restore(
            UUID id,
            UUID accountId,
            UUID professionalId,
            ConversationStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new Conversation(id, accountId, professionalId, status, createdAt, updatedAt);
    }

    public void close(Instant now) {
        if (status == ConversationStatus.CLOSED) {
            throw new IllegalStateException("conversation already closed");
        }
        status = ConversationStatus.CLOSED;
        updatedAt = now;
    }

    public boolean isOpen() {
        return status == ConversationStatus.OPEN;
    }

    public UUID id() {
        return id;
    }

    public UUID accountId() {
        return accountId;
    }

    public UUID professionalId() {
        return professionalId;
    }

    public ConversationStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
