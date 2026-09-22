# MVP-02 — Validação Qualidade (Conversation + Message)

Card: MVP-02 (Issue #4)  
Requisitos: REQ-F-002, REQ-F-003 (persistência e evento; submit HTTP em MVP-03)

## Veredito

**PASS** quando `docker compose up -d postgres` e `mvn clean test` executam com sucesso (PostgreSQL em `localhost:55432`).

## Evidência reproduzível

```bash
docker compose up -d postgres
cd backend && mvn clean test
```

Esperado:

```text
Tests run: 40, Failures: 0, Errors: 0
```

| Classe | Testes |
|--------|--------|
| `com.workchatia.identity.AuthenticationControllerTest` | 11 |
| `com.workchatia.identity.infrastructure.security.JwtServiceTest` | 2 |
| `com.workchatia.conversation.domain.ConversationTest` | 4 |
| `com.workchatia.conversation.application.ConversationApplicationServiceTest` | 2 |
| `com.workchatia.message.domain.MessageTest` | 6 |
| `com.workchatia.message.application.MessageApplicationServiceTest` | 4 |
| `com.workchatia.conversation.ConversationMessageControllerTest` | 10 |
| `com.workchatia.message.MessageOutboxAtomicityIntegrationTest` | 1 |

## Mapeamento aceite (issue #4) → teste

| Aceite | Cenário | Método |
|--------|---------|--------|
| Criar Conversation | POST 201 | `createConversationAppearsInListForSameAccount` |
| Listar da Account | GET page | `createConversationAppearsInListForSameAccount` |
| Consultar Conversation | GET `{id}` 200 | `getConversationByIdReturns200ForOwner` |
| Cross-account Conversation | 404 | `crossAccountConversationAccessReturns404` |
| Conversation encerrada | POST message 409 | `closedConversationRejectsNewMessage` |
| Criar Message DRAFT | POST 201 | `createMessagePersistsDraftAndOutboxEvent` |
| Listar / consultar Message | GET list + GET `{id}` | `listAndGetMessageReturns200` |
| MessageCreated + outbox (contrato v1) | JSON payload | `createMessagePersistsDraftAndOutboxEvent` |
| Editar Message DRAFT | PATCH 200 | `editOwnDraftMessageReturns200` |
| Editar por não-autor | PATCH 403 | `editMessageByNonAuthorReturns403` |
| SEND_MESSAGE | 403 viewer | `createMessageWithoutPermissionReturns403` |
| Isolamento Message | 404 outra Account | `crossAccountMessageListReturns404` |
| Atomicidade Message + Outbox | rollback | `MessageOutboxAtomicityIntegrationTest.outboxFailureRollsBackMessageInsert` |
| AC-003 | criar + listar | `createConversationAppearsInListForSameAccount` |

## Correções QA (Q-201 … Q-206)

| ID | Cobertura |
|----|-----------|
| Q-201 | `getConversationByIdReturns200ForOwner`, `listAndGetMessageReturns200` |
| Q-202 | `editMessageByNonAuthorReturns403` + seed `V4__identity_seed_carol_professional.sql` |
| Q-203 | Payload JSON assert em `createMessagePersistsDraftAndOutboxEvent` |
| Q-204 | `MessageOutboxAtomicityIntegrationTest` |
| Q-205 | `MessageApplicationServiceTest`, `ConversationApplicationServiceTest` |
| Q-206 | Javadoc `MessageCreatedEvent` + nota consumidor DRAFT neste doc |

## Decisões técnicas

| Decisão | Detalhe |
|---------|---------|
| Message em DRAFT + MessageCreated | Consumidores futuros MUST ignorar até `PENDING_REVIEW` (submit MVP-03) |
| Cross-account | 404 via `findByIdAndAccount` |
| Outbox | Persistência apenas; `published_at` NULL até worker MVP-06 |
| Erros de domínio | `IllegalStateException` / `DomainConflictException` → HTTP 409 |

## Revalidação (2026-09-22 — correções QA)

- Seed dev/test: `V4__identity_seed_carol_professional.sql` (Carol PROFESSIONAL, Account Alpha)
- `mvn -B clean test` → **40** testes, **0** falhas
- CI: mínimo **38** testes no guard do `backend-ci.yml`

## Riscos residuais

| Risco | Mitigação |
|-------|-----------|
| DB local sem V4 | `flyway migrate` ou recriar volume Postgres |
| FK não testada por violação SQL | Flyway + `ddl-auto: validate` |

## Fora do escopo

Review, LLM, Delivery, publicador Redis, remoção do probe `POST /api/v1/messages`.

## Próximo gate

MVP-03 CommunicationPolicy + submit HTTP para revisão.
