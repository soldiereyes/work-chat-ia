# Estrutura de pacotes

Organização **orientada ao domínio** (não `controller/service/entity` plano).

```text
src/main/java/com/workchatia/

├── conversation/
│   ├── domain/
│   │   ├── Conversation.java
│   │   ├── ConversationStatus.java
│   │   └── ConversationRepository.java
│   ├── application/
│   │   ├── ConversationApplicationService.java
│   │   └── CreateConversationCommand.java
│   └── infrastructure/
│       └── JpaConversationRepository.java
│
├── message/
│   ├── domain/
│   │   ├── Message.java
│   │   ├── MessageStatus.java
│   │   ├── MessageType.java
│   │   └── MessageRepository.java
│   ├── application/
│   │   ├── MessageApplicationService.java
│   │   └── commands/
│   └── infrastructure/
│       └── JpaMessageRepository.java
│
├── review/
│   ├── domain/
│   │   ├── MessageReview.java
│   │   ├── ReviewSuggestion.java
│   │   ├── ReviewStatus.java
│   │   └── CommunicationPolicy.java
│   ├── application/
│   │   ├── MessageReviewService.java
│   │   └── AIReviewService.java
│   └── infrastructure/
│       ├── OpenAIReviewProvider.java
│       └── ReviewEventListener.java
│
├── delivery/
│   ├── application/
│   │   └── MessageDeliveryService.java
│   └── infrastructure/
│       └── ChannelGateway.java
│
├── identity/
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
└── shared/
    ├── domain/
    │   └── event/
    │       ├── DomainEvent.java
    │       ├── MessageCreatedEvent.java
    │       └── OutboxRepository.java
    └── infrastructure/
        └── JpaOutboxRepository.java
```

## Responsabilidade por pacote

| Pacote | Dono do conceito |
|--------|------------------|
| `conversation` | `Conversation`, ciclo de vida |
| `message` | `Message`, aprovação, persistência |
| `review` | `MessageReview`, políticas, IA |
| `delivery` | Envio ao canal externo |
| `identity` | AuthN/AuthZ, `User`, `Account` |
