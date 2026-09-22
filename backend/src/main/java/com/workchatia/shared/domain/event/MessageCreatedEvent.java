package com.workchatia.shared.domain.event;

import com.workchatia.message.domain.MessageType;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a message row is persisted (version 1).
 * <p>At MVP-02 the message is typically in {@code DRAFT}; review workers (MVP-06) MUST
 * ignore or filter until the message reaches {@code PENDING_REVIEW} after submit (MVP-03).
 */
public record MessageCreatedEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID accountId,
        UUID conversationId,
        UUID messageId,
        UUID authorId,
        MessageType messageType)
        implements DomainEvent {

    public static final String TYPE = "MessageCreated";
    public static final int EVENT_VERSION = 1;

    public static MessageCreatedEvent create(
            UUID eventId,
            Instant occurredAt,
            UUID accountId,
            UUID conversationId,
            UUID messageId,
            UUID authorId,
            MessageType messageType) {
        return new MessageCreatedEvent(
                eventId,
                TYPE,
                EVENT_VERSION,
                occurredAt,
                accountId,
                conversationId,
                messageId,
                authorId,
                messageType);
    }
}
