package com.workchatia.message.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, UUID> {

    List<MessageEntity> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);
}
