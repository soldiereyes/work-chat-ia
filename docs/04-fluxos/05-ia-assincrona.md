# Fluxo 5 — IA assíncrona (recomendado)

Para escalar, **não deixe a requisição HTTP esperar o LLM**.

```mermaid
sequenceDiagram
    autonumber

    actor Professional as Profissional
    participant ChatUI as Chat Interface
    participant MessageController
    participant MessageApplicationService
    participant MessageRepository
    participant EventPublisher
    participant MessageReviewWorker
    participant MessageReviewService
    participant AIReviewService
    participant LLMProvider
    participant ReviewRepository
    participant RealtimeGateway

    Professional->>ChatUI: Envia mensagem

    ChatUI->>MessageController: POST /messages

    MessageController->>MessageApplicationService: createMessage(command)

    MessageApplicationService->>MessageRepository: save(message)

    MessageRepository-->>MessageApplicationService: Message

    MessageApplicationService->>EventPublisher: publish(MessageCreated)

    MessageApplicationService-->>MessageController: MessageCreatedResponse

    MessageController-->>ChatUI: 202 Accepted

    EventPublisher->>MessageReviewWorker: MessageCreated

    MessageReviewWorker->>MessageReviewService: review(messageId)

    MessageReviewService->>AIReviewService: analyze(message)

    AIReviewService->>LLMProvider: review(context, content)

    LLMProvider-->>AIReviewService: AIReviewResponse

    AIReviewService-->>MessageReviewService: ReviewResult

    MessageReviewService->>ReviewRepository: save(review)

    MessageReviewService->>RealtimeGateway: publish(ReviewCompleted)

    RealtimeGateway-->>ChatUI: ReviewCompleted

    ChatUI-->>Professional: Exibe resultado da revisão
```

## Decisão importante

Use **`MessageCreated`** como domain event — não `SendMessageToAI`.

O evento pertence ao domínio de mensagens. A IA é consequência desse evento, o que torna o sistema mais extensível.
