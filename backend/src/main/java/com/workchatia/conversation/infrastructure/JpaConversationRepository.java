package com.workchatia.conversation.infrastructure;

import com.workchatia.conversation.domain.Conversation;
import com.workchatia.conversation.domain.ConversationRepository;
import com.workchatia.conversation.infrastructure.persistence.ConversationEntity;
import com.workchatia.conversation.infrastructure.persistence.ConversationJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class JpaConversationRepository implements ConversationRepository {

    private final ConversationJpaRepository jpa;

    public JpaConversationRepository(ConversationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Conversation save(Conversation conversation) {
        ConversationEntity entity = toEntity(conversation);
        ConversationEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Conversation> findByIdAndAccount(UUID id, UUID accountId) {
        return jpa.findByIdAndAccountId(id, accountId).map(JpaConversationRepository::toDomain);
    }

    @Override
    public Page<Conversation> findByAccount(UUID accountId, Pageable pageable) {
        return jpa.findByAccountId(accountId, pageable).map(JpaConversationRepository::toDomain);
    }

    private static ConversationEntity toEntity(Conversation conversation) {
        ConversationEntity entity = new ConversationEntity();
        entity.setId(conversation.id());
        entity.setAccountId(conversation.accountId());
        entity.setProfessionalId(conversation.professionalId());
        entity.setStatus(conversation.status());
        entity.setCreatedAt(conversation.createdAt());
        entity.setUpdatedAt(conversation.updatedAt());
        return entity;
    }

    private static Conversation toDomain(ConversationEntity entity) {
        return Conversation.restore(
                entity.getId(),
                entity.getAccountId(),
                entity.getProfessionalId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
