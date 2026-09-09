# Work Chat IA

Plataforma de comunicação em que o **profissional escreve uma mensagem**, o sistema **avalia pelas regras de comunicação**, a **IA sugere ajustes**, o **profissional decide** e, então, a **mensagem é enviada**.

## Documentação

A documentação completa está em [`docs/`](docs/README.md).

| Seção | Conteúdo |
|-------|----------|
| [Produto](docs/01-produto/visao-geral.md) | Visão, proposta de valor, personas e glossário |
| [Domínio](docs/02-dominio/bounded-contexts.md) | Bounded contexts, entidades, estados e eventos |
| [Arquitetura](docs/03-arquitetura/visao-geral.md) | Stack, pacotes, classes e decisões |
| [Fluxos](docs/04-fluxos/README.md) | Diagramas sequenciais Mermaid do MVP |
| [Regras de negócio](docs/05-regras-negocio/principios-review.md) | Políticas de comunicação, áudio e revisão |
| [Roadmap](docs/06-roadmap/mvp.md) | Escopo do MVP e evolução futura |
| [Referências](docs/07-referencias/chatwoot.md) | Análise técnica do Chatwoot como referência |

## Princípio central

> A IA **revisa e sugere**; o profissional **decide**.

> **Review** não envia mensagem, não controla usuário e não controla conversa — apenas avalia uma mensagem e produz um `MessageReview`.

## Stack prevista (MVP)

- **Backend:** Java / Spring Boot (monólito modular)
- **Frontend:** React
- **Banco:** PostgreSQL (source of truth)
- **Cache/Filas:** Redis + workers assíncronos
- **Realtime:** WebSocket
- **IA:** `LLMProvider` (interface plugável)
