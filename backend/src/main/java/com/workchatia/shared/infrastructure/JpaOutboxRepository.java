package com.workchatia.shared.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workchatia.shared.domain.event.DomainEvent;
import com.workchatia.shared.domain.event.MessageCreatedEvent;
import com.workchatia.shared.domain.event.OutboxRepository;
import com.workchatia.shared.infrastructure.persistence.OutboxEventEntity;
import com.workchatia.shared.infrastructure.persistence.OutboxEventJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOutboxRepository implements OutboxRepository {

    private final OutboxEventJpaRepository jpa;
    private final ObjectMapper objectMapper;

    public JpaOutboxRepository(OutboxEventJpaRepository jpa, ObjectMapper objectMapper) {
        this.jpa = jpa;
        this.objectMapper = objectMapper;
    }

    @Override
    public void append(DomainEvent event) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.setId(event.eventId());
        entity.setEventType(event.eventType());
        entity.setVersion(event.version());
        entity.setOccurredAt(event.occurredAt());
        entity.setPayload(serializePayload(event));
        jpa.save(entity);
    }

    private String serializePayload(DomainEvent event) {
        try {
            if (event instanceof MessageCreatedEvent created) {
                return objectMapper.writeValueAsString(
                        new MessageCreatedPayload(
                                created.eventId(),
                                created.eventType(),
                                created.version(),
                                created.occurredAt(),
                                created.accountId(),
                                created.conversationId(),
                                created.messageId(),
                                created.authorId(),
                                created.messageType().name()));
            }
            throw new IllegalArgumentException("Unsupported event type: " + event.eventType());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize outbox payload", ex);
        }
    }

    private record MessageCreatedPayload(
            java.util.UUID eventId,
            String eventType,
            int version,
            java.time.Instant occurredAt,
            java.util.UUID accountId,
            java.util.UUID conversationId,
            java.util.UUID messageId,
            java.util.UUID authorId,
            String messageType) {}
}
