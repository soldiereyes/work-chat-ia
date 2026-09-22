package com.workchatia.conversation.application;

import com.workchatia.conversation.domain.Conversation;
import com.workchatia.conversation.domain.ConversationRepository;
import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.shared.application.ResourceNotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversationApplicationService {

    private final ConversationRepository conversationRepository;
    private final Clock clock;

    public ConversationApplicationService(ConversationRepository conversationRepository, Clock clock) {
        this.conversationRepository = conversationRepository;
        this.clock = clock;
    }

    @Transactional
    public Conversation create(AuthenticatedUser user) {
        requireUser(user);
        Instant now = clock.instant();
        Conversation conversation = Conversation.create(UUID.randomUUID(), user.accountId(), user.userId(), now);
        return conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public Page<Conversation> list(AuthenticatedUser user, Pageable pageable) {
        requireUser(user);
        return conversationRepository.findByAccount(user.accountId(), pageable);
    }

    @Transactional(readOnly = true)
    public Conversation get(AuthenticatedUser user, UUID conversationId) {
        requireUser(user);
        return conversationRepository
                .findByIdAndAccount(conversationId, user.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("conversation_not_found"));
    }

    @Transactional
    public Conversation close(AuthenticatedUser user, UUID conversationId) {
        requireUser(user);
        Conversation conversation = conversationRepository
                .findByIdAndAccount(conversationId, user.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("conversation_not_found"));
        conversation.close(clock.instant());
        return conversationRepository.save(conversation);
    }

    private static void requireUser(AuthenticatedUser user) {
        if (user == null) {
            throw new ResourceNotFoundException("conversation_not_found");
        }
    }
}
