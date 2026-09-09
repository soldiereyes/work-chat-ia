# Stack do MVP

## Camadas

| Camada | Tecnologia | Papel |
|--------|------------|-------|
| Frontend | React | Chat Interface, exibição de `ReviewResult` |
| API | Spring Boot | Controllers, application services, domínio |
| Banco | PostgreSQL | Persistência relacional |
| Cache/Fila | Redis | Filas de workers, pub/sub |
| Workers | Spring + Redis (ou RabbitMQ/Kafka depois) | `MessageReviewWorker` |
| Realtime | WebSocket | `RealtimeGateway` → UI |
| IA | `LLMProvider` | OpenAI ou outro provedor |

## O que entra no MVP

```text
User, Account
Conversation, Message
MessageReview, ReviewSuggestion, CommunicationPolicy
MessageApplicationService, MessageReviewService, AIReviewService
MessageRepository, ConversationRepository, MessageReviewRepository
LLMProvider
```

## O que fica para depois

```text
ChannelGateway (WhatsApp, Teams, Instagram, ...)
Webhook outbound
RabbitMQ / Kafka
OpenSearch
Integração Chatwoot
Keycloak (se Identity externo)
```

## Referência Chatwoot (stack original)

```text
Rails + Vue + PostgreSQL + Redis + Sidekiq + ActionCable + S3
```

Nossa stack equivalente:

```text
Spring Boot + React + PostgreSQL + Redis + Workers + WebSocket
```
