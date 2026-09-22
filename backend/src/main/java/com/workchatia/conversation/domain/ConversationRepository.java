package com.workchatia.conversation.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConversationRepository {

    Conversation save(Conversation conversation);

    Optional<Conversation> findByIdAndAccount(UUID id, UUID accountId);

    Page<Conversation> findByAccount(UUID accountId, Pageable pageable);
}
