package com.workchatia.conversation.infrastructure.web;

import com.workchatia.conversation.application.ConversationApplicationService;
import com.workchatia.conversation.domain.Conversation;
import com.workchatia.conversation.domain.ConversationStatus;
import com.workchatia.identity.domain.AuthenticatedUser;
import com.workchatia.identity.infrastructure.web.SecuritySupport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final ConversationApplicationService conversationApplicationService;

    public ConversationController(ConversationApplicationService conversationApplicationService) {
        this.conversationApplicationService = conversationApplicationService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> create() {
        AuthenticatedUser user = SecuritySupport.currentUser();
        Conversation conversation = conversationApplicationService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ConversationResponse.from(conversation));
    }

    @GetMapping
    public Page<ConversationResponse> list(Pageable pageable) {
        AuthenticatedUser user = SecuritySupport.currentUser();
        return conversationApplicationService.list(user, pageable).map(ConversationResponse::from);
    }

    @GetMapping("/{conversationId}")
    public ConversationResponse get(@PathVariable UUID conversationId) {
        AuthenticatedUser user = SecuritySupport.currentUser();
        return ConversationResponse.from(conversationApplicationService.get(user, conversationId));
    }

    @PatchMapping("/{conversationId}")
    public ConversationResponse update(
            @PathVariable UUID conversationId, @Valid @RequestBody UpdateConversationRequest request) {
        AuthenticatedUser user = SecuritySupport.currentUser();
        if (!"close".equalsIgnoreCase(request.action())) {
            throw new IllegalArgumentException("unsupported_action");
        }
        return ConversationResponse.from(conversationApplicationService.close(user, conversationId));
    }

    public record UpdateConversationRequest(@NotBlank String action) {}

    public record ConversationResponse(
            UUID id,
            UUID accountId,
            UUID professionalId,
            ConversationStatus status,
            Instant createdAt,
            Instant updatedAt) {

        static ConversationResponse from(Conversation conversation) {
            return new ConversationResponse(
                    conversation.id(),
                    conversation.accountId(),
                    conversation.professionalId(),
                    conversation.status(),
                    conversation.createdAt(),
                    conversation.updatedAt());
        }
    }
}
