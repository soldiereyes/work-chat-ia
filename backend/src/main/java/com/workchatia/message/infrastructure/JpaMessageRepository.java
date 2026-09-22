package com.workchatia.message.infrastructure;

import com.workchatia.message.domain.Message;
import com.workchatia.message.domain.MessageRepository;
import com.workchatia.message.infrastructure.persistence.MessageEntity;
import com.workchatia.message.infrastructure.persistence.MessageJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaMessageRepository implements MessageRepository {

    private final MessageJpaRepository jpa;

    public JpaMessageRepository(MessageJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Message save(Message message) {
        MessageEntity entity = toEntity(message);
        MessageEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return jpa.findById(id).map(JpaMessageRepository::toDomain);
    }

    @Override
    public List<Message> findByConversationId(UUID conversationId) {
        return jpa.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(JpaMessageRepository::toDomain)
                .toList();
    }

    private static MessageEntity toEntity(Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setId(message.id());
        entity.setConversationId(message.conversationId());
        entity.setAuthorId(message.authorId());
        entity.setContent(message.content());
        entity.setType(message.type());
        entity.setStatus(message.status());
        entity.setCreatedAt(message.createdAt());
        entity.setUpdatedAt(message.updatedAt());
        return entity;
    }

    private static Message toDomain(MessageEntity entity) {
        return Message.restore(
                entity.getId(),
                entity.getConversationId(),
                entity.getAuthorId(),
                entity.getContent(),
                entity.getType(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
