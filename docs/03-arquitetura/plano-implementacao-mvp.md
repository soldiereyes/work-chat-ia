# Plano de implementação do MVP

Plano técnico derivado do [MVP Product Contract](../06-roadmap/mvp-product-contract.md) e do handoff [MVP-00](../06-roadmap/handoff-mvp-00-produto-implementacao.yaml).

## Estado

| Fatia | Estado | Evidência |
|-------|--------|-----------|
| MVP-00 Produto | `PRODUCT_READY` | `mvp-product-contract.md` |
| MVP-01 Identity & Access | **Fechado** | `identity/`, Flyway V1 + seed dev/test, `AuthenticationControllerTest` + `JwtServiceTest`, [validação Qualidade](../06-roadmap/mvp-01-validacao-qualidade.md) |
| MVP-02 … MVP-10 | Planejado | Seções abaixo |

## Decisões (ADRs)

Ver [decisoes-arquiteturais.md](decisoes-arquiteturais.md) ADR-001 … ADR-012.

## Contratos API (visão)

| Endpoint | Fatia | REQ / AC |
|----------|-------|----------|
| `POST /api/v1/auth/login` | MVP-01 | REQ-F-001, AC-001 |
| `GET /api/v1/me` | MVP-01 | perfil do token |
| `GET /api/v1/users/{id}` | MVP-01 | AC-020 (isolamento) |
| `POST /api/v1/messages` (probe authZ) | MVP-01 | AC-002 |
| `GET/POST /api/v1/conversations` | MVP-02 | REQ-F-002 |
| `POST /api/v1/conversations/{id}/messages` (202) | MVP-03, MVP-06 | REQ-F-003, REQ-F-010 |
| `PUT /api/v1/messages/{id}` | MVP-08 | REQ-F-008 |
| `POST /api/v1/messages/{id}/approve` | MVP-08 | REQ-F-007 |
| `POST /api/v1/messages/audio` | MVP-09 | REQ-F-011 |

## Eventos

| Evento | Publicador | Consumidor | Fatia |
|--------|------------|------------|-------|
| `MessageCreated` | Message Communication | Review worker | MVP-06 |
| `ReviewCompleted` | Review / RealtimeGateway | UI WebSocket | MVP-06 |

## Fatias MVP-01 … MVP-10

| Fatia | Módulos | Fluxos | Handoff DBA |
|-------|---------|--------|-------------|
| MVP-01 | `identity` | 07 | `V1__identity.sql` |
| MVP-02 | `conversation` | 08 | `conversation` tables |
| MVP-03 | `message` | 01, 05 | `message` tables |
| MVP-04 | `review` policy | 04 | — |
| MVP-05 | `review` + LLM sync path | 01 | — |
| MVP-06 | events, worker, WS | 05 | Redis/outbox (PD-07) |
| MVP-07 | review UX BLOCKED | 04 | — |
| MVP-08 | approve, edit | 02, 03 | — |
| MVP-09 | `delivery` mock, áudio | 02, 06 | — |
| MVP-10 | E2E qualidade | 08 | — |

## Reconciliação do esboço existente

- `Message.approve()` sem validação de `ReviewResult` → corrigir em MVP-07/MVP-08 (defeito Q-003).
- `MessageReviewService` / `DefaultCommunicationPolicy` → evoluir em MVP-04+.
- Revisão assíncrona (ADR-002, ADR-012) é dona do fluxo HTTP 202 em MVP-06.

## Pendências documentadas (não bloqueiam MVP-01)

- PD-05 Delivery como BC separado
- PD-06 Ordem Policy + IA no worker
- PD-07 Outbox / idempotência `MessageCreated`

## Próximo gate

MVP-01 concluído → Qualidade valida AC-001, AC-002, AC-020 (escopo Identity) → MVP-02 Conversation.
