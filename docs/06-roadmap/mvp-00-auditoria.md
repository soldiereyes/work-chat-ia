# MVP-00 — Auditoria documental (evidência)

Data: 2026-09-21  
Card: MVP-00 / Issue #2  
Escopo: somente leitura da documentação e estado do repositório; sem alteração de código de produção neste artefato.

## 1. Estado do repositório

| Área | Situação |
|------|----------|
| Documentação em `docs/` | Completa para produto, domínio, arquitetura, fluxos (8), regras de negócio, roadmap |
| Skills em `skills/` | AGENT_ROUTER, PIPELINE, DEFINITION_OF_DONE, 6 skills de área |
| Código backend | Esboço parcial em `backend/` (domínio message/review); **fora do escopo do MVP-00** |
| Cards MVP-01 … MVP-10 | **Não encontrados** no repositório (grep, issues via `gh` indisponível) |
| `mvp-product-contract.md` | Criado neste card |

## 2. Requisitos já definidos (fontes)

- Entregáveis funcionais: `docs/06-roadmap/mvp.md`
- Fora do escopo: `docs/06-roadmap/fora-do-mvp.md`
- Atores e valor: `docs/01-produto/*`
- Estados, entidades, eventos: `docs/02-dominio/*`
- ADRs e stack: `docs/03-arquitetura/*`
- Comportamentos observáveis: `docs/04-fluxos/*`
- Regras: `docs/05-regras-negocio/*`

## 3. Requisitos implícitos (inferidos, sustentados por mais de um doc)

- Profissional deve estar autenticado e autorizado para enviar mensagem (`04-fluxos/07`, `06-roadmap/mvp.md`).
- Mensagem pertence a uma `Conversation` existente (`02-dominio/entidades.md`, fluxo 1).
- Após aprovação explícita, sistema entrega via mecanismo de delivery (`02-aprovacao.md`, `visao-geral.md`).
- UI deve refletir estados de carregamento/erro no fluxo assíncrono (skill Desenvolvedores — tratado como expectativa de UX, não novo requisito de produto).

## 4. Requisitos ausentes ou pouco especificados

| Gap | Impacto | Classificação |
|-----|---------|----------------|
| Definição oficial dos cards MVP-01 … MVP-10 | Rastreabilidade card ↔ requisito | **BACKLOG GAP** |
| Critério objetivo de “tempo aceitável” para revisão (`mvp.md` critério de sucesso #1) | Teste de performance | **PRODUCT_DECISION_REQUIRED** (métrica) ou aceitar apenas “sem bloquear HTTP” (ADR-002) |
| Se áudio passa por `MessageReview` ou só autorização + delivery | Comportamento de produto | **PRODUCT_DECISION_REQUIRED** (fluxo 6 não mostra revisão) |
| Ordem exata Policy vs IA quando ambas aplicam em async | Comportamento | **ARCHITECTURAL_DECISION_REQUIRED** (Implementação) |
| Conteúdo mínimo de `CommunicationPolicy` no MVP (quais regras obrigatórias além dos exemplos) | Aceite de policy | **PRODUCT_DECISION_REQUIRED** (lista exemplificativa em `communication-policy.md`) |
| `IdentityProvider` no login (local vs externo) | Implementação auth | **ARCHITECTURAL_DECISION_REQUIRED** (Keycloak está fora do MVP) |

## 5. Conflitos documentados (não resolvidos silenciosamente)

### C-01 — Revisão síncrona vs assíncrona

| Fonte A | Fonte B |
|---------|---------|
| `04-fluxos/01-envio-para-revisao.md`: revisão na mesma requisição HTTP, resposta `200 OK` + `ReviewResult` | `03-arquitetura/decisoes-arquiteturais.md` ADR-002: `POST` retorna `202 Accepted`; worker processa LLM |
| `06-roadmap/mvp.md` passo 4: “MessageReview (síncrono, para validar UX)” | `06-roadmap/mvp.md` passo 5: “IA assíncrona + WebSocket” |

**Resolução Produto:** o MVP contempla **ambas as fases**: protótipo/validação UX pode usar caminho síncrono; **comportamento alvo de produção no MVP** é assíncrono para LLM (ADR-002). Policy determinística pode rodar antes da persistência em qualquer modo — detalhe de orquestração → Implementação.

### C-02 — Número de bounded contexts

| Fonte A | Fonte B |
|---------|---------|
| `02-dominio/bounded-contexts.md`: **quatro** contextos (sem Delivery explícito) | `skills/implementacao/SKILL.md` e `03-arquitetura/visao-geral.md`: **Delivery** como contexto/módulo |

**Resolução Produto:** Delivery é capacidade de produto (“entregar ao cliente após aprovação”); fronteira de contexto → **ARCHITECTURAL_DECISION_REQUIRED** (quarto vs quinto BC).

### C-03 — ChannelGateway no MVP

| Fonte A | Fonte B |
|---------|---------|
| `03-arquitetura/stack-mvp.md`: ChannelGateway “para depois” | `06-roadmap/mvp.md`: “Aprovação e delivery (**mock** de ChannelGateway)” |
| `06-roadmap/fora-do-mvp.md`: lista ChannelGateway em integrações futuras |

**Resolução Produto:** no MVP, **entrega ao canal externo real está fora**; é requisito ter **delivery com adapter mockável** (`ChannelGateway` como interface + implementação mock). Integrações WhatsApp/Teams etc. permanecem fora do MVP.

### C-04 — Fluxo 01 vs Fluxo 05 (ordem persistência × revisão)

Fluxo 01: `MessageReviewService.review` antes de `save`.  
Fluxo 05: `save` → `MessageCreated` → worker → review.

**Resolução:** não é contradição de regra de negócio; é **decisão de sequência técnica** por modo sync/async → **ARCHITECTURAL_DECISION_REQUIRED**.

### C-05 — Áudio sem etapa de revisão no diagrama

`04-fluxos/06-audio.md` envia áudio direto a `MessageDeliveryService` sem `MessageReview`.

**Registro:** **PRODUCT_DECISION_REQUIRED** — confirmar se áudio no MVP ignora revisão semântica ou se é omissão do diagrama.

## 6. Decisões que pertencem à Implementação (não decididas por Produto neste card)

- Classes, pacotes, endpoints REST exatos, schema SQL, estratégia outbox, fila Redis vs alternativa.
- Unificação dos fluxos sequenciais 01 e 05 em um único contrato de API.
- Tradução HTTP de erros de domínio.

## 7. Conclusão da auditoria

A base documental é **suficiente** para consolidar o Product Contract do MVP, com pendências explícitas acima. O MVP-00 **não está BLOCKED** para entrega documental; implementação deve tratar decisões arquiteturais listadas antes de codificar o caminho único de revisão.
