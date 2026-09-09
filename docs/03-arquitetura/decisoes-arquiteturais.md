# Decisões arquiteturais

## ADR-001: Monólito modular

**Decisão:** Spring Boot monólito com pacotes por bounded context.

**Motivo:** Reduz complexidade operacional; Chatwoot prova que escala horizontal é possível sem microsserviços.

**Alternativa rejeitada:** Microsserviços desde o dia 1.

---

## ADR-002: Revisão assíncrona por padrão

**Decisão:** `POST /messages` retorna `202 Accepted`; revisão via `MessageCreated` + worker.

**Motivo:** LLM não deve bloquear requisição HTTP; permite escalar workers independentemente.

**Alternativa:** Revisão síncrona apenas para protótipo local.

---

## ADR-003: Evento `MessageCreated` (não `SendMessageToAI`)

**Decisão:** Domain event nomeado pelo domínio de mensagens.

**Motivo:** Extensibilidade — outros listeners podem reagir (auditoria, métricas) sem acoplar à IA.

---

## ADR-004: Review como bounded context isolado

**Decisão:** `MessageReviewService` não chama `MessageDeliveryService`.

**Motivo:** Evitar dependência circular e manter revisão testável isoladamente.

---

## ADR-005: Estados separados (`MessageStatus` vs `ReviewStatus`)

**Decisão:** Dois enums distintos.

**Motivo:** Mensagem e revisão têm ciclos de vida independentes.

---

## ADR-006: `Suggestion` ≠ `Block`

**Decisão:** `ReviewResult` distingue `SUGGESTION` e `BLOCKED`.

**Motivo:** UX e regras de negócio diferentes — sugestão é orientação; bloqueio é impedimento.

---

## ADR-007: Áudio via política, não `if` no frontend

**Decisão:** `AudioMessagePolicy` + `AuthorizationService` no backend.

**Motivo:** Regra de negócio não pode depender apenas da UI.

---

## ADR-008: Dependências explícitas em serviços críticos

**Decisão:** Evitar padrão `Current.account` implícito (como no Chatwoot) em serviços de revisão e entrega.

**Motivo:** Testabilidade e clareza de contratos em Java/Spring.
