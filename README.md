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

## Skills e pipeline de desenvolvimento

As skills operacionais ficam em [`skills/`](skills/README.md).

O fluxo oficial é:

```text
Produto
   ↓
Implementação
   ├── Desenvolvedores
   └── DBA
         ↓
      Qualidade
         ↓
       DevOps
         ↓
      Release
         ↓
     Operação
         ↺
      Produto
```

Consulte [`skills/PIPELINE.md`](skills/PIPELINE.md) para os gates, handoffs, estados e regras de rework.

Para orientar agentes de IA, use [`skills/AGENT_ROUTER.md`](skills/AGENT_ROUTER.md). O checklist global está em [`skills/DEFINITION_OF_DONE.md`](skills/DEFINITION_OF_DONE.md).

## Princípio central

> A IA **revisa e sugere**; o profissional **decide**.

> **Review** não envia mensagem, não controla usuário e não controla conversa — apenas avalia uma mensagem e produz um `MessageReview`.

## Backend (desenvolvimento local)

1. Subir infraestrutura:

```bash
docker compose up -d
```

O PostgreSQL do compose expõe a porta **55432** no host (evita conflito com Postgres local na 5432).

2. Rodar a API:

```bash
export DB_PORT=55432
cd backend && mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

3. Usuários de seed (perfil `dev`): `alice@workchat.test` / `secret123` (com `SEND_MESSAGE`); `viewer@workchat.test` / `secret123` (sem envio).

4. Testes (exige Postgres do compose em execução):

```bash
docker compose up -d postgres
cd backend && mvn test
```

Usa `jdbc:postgresql://localhost:55432/workchat` por padrão (`TEST_JDBC_URL` para sobrescrever). Flyway aplica `migration` + `migration-dev` (seed).

Health: `GET http://localhost:8080/actuator/health`

## Stack prevista (MVP)

- **Backend:** Java / Spring Boot (monólito modular)
- **Frontend:** React
- **Banco:** PostgreSQL (source of truth)
- **Cache/Filas:** Redis + workers assíncronos
- **Realtime:** WebSocket
- **IA:** `LLMProvider` (interface plugável)
