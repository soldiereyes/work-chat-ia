# Escopo do MVP

## Objetivo

Provar o **núcleo do produto**:

```text
Professional → Message → MessageReview → ReviewResult → Approve → Delivery → Cliente
```

## Entregáveis funcionais

- [ ] Login e autorização básica (`User`, `Account`, `Role`, `Permission`)
- [ ] Criar e listar `Conversation`
- [ ] Criar `Message` e submeter para revisão
- [ ] `CommunicationPolicy` (regras determinísticas)
- [ ] `AIReviewService` + `LLMProvider`
- [ ] Exibir `ReviewResult` e `ReviewSuggestion` na UI
- [ ] Aprovar, editar e reenviar mensagem
- [ ] Bloquear envio quando `BLOCKED`
- [ ] Revisão assíncrona (`MessageCreated` + worker + WebSocket)
- [ ] Política de áudio para usuários autorizados

## Componentes de código (primeira implementação)

```text
User, Account
Conversation, Message
MessageReview, ReviewSuggestion, CommunicationPolicy

MessageApplicationService
MessageReviewService
AIReviewService

MessageRepository
ConversationRepository
MessageReviewRepository

LLMProvider
```

## Critérios de sucesso

1. Profissional envia mensagem e recebe revisão em tempo aceitável.
2. Sugestões da IA são compreensíveis e acionáveis.
3. Profissional mantém controle final sobre o envio.
4. Estados de `Message` e `MessageReview` são consistentes e auditáveis.

## Ordem sugerida de implementação

1. Identity & Access (auth básico)
2. Conversation + Message (CRUD)
3. CommunicationPolicy (regras simples)
4. MessageReview (síncrono, para validar UX)
5. IA assíncrona + WebSocket
6. Aprovação e delivery (mock de `ChannelGateway`)
7. Política de áudio
