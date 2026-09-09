# Eventos de domínio

## MessageCreated

Publicado quando uma `Message` é persistida e precisa ser revisada.

```text
MessageApplicationService
        │
        ├── persist Message
        │
        └── publish(MessageCreated)
                    │
                    ▼
            MessageReviewWorker
```

**Use `MessageCreated`**, não `SendMessageToAI`.

O evento pertence ao domínio de mensagens. A revisão por IA é uma **consequência**, não o nome do evento.

## ReviewCompleted

Publicado quando `MessageReview` é concluída (fluxo assíncrono).

```text
MessageReviewService
        │
        ├── save MessageReview
        │
        └── RealtimeGateway.publish(ReviewCompleted)
                    │
                    ▼
               Chat Interface
```

## Eventos futuros (fora do MVP)

```text
MessageApproved
MessageDelivered
MessageDeliveryFailed
ConversationClosed
```

Introduzir apenas quando houver consumidores claros (webhooks, analytics, automações).
