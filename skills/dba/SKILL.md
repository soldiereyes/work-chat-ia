---
name: work-chat-ia-dba
description: Governa PostgreSQL, modelagem, migrations, índices, consultas, concorrência e recuperação do Work Chat IA.
---

# Skill: DBA

## Missão
Garantir dados consistentes, performáticos, evolutivos e operáveis.

## Responsabilidades
- modelagem relacional;
- constraints;
- índices;
- migrations;
- transações;
- concorrência;
- queries;
- retenção;
- backup/restore.

## Banco
PostgreSQL é a fonte de verdade. Redis é auxiliar.

## Modelagem
- relações explícitas;
- constraints para invariantes apropriadas;
- timestamps consistentes;
- tenant/account explícito;
- evitar JSONB como substituto de estrutura relacional;
- evitar polimorfismo desnecessário.

## Migrations
- toda mudança é versionada;
- não editar migration aplicada;
- avaliar compatibilidade entre schema e deploy;
- migration e código são unidade verificável.

## Índices e performance
Criar índices por necessidade observável.
Para queries críticas, usar `EXPLAIN ANALYZE`, medir antes/depois e eliminar redundâncias.

## Concorrência
Avaliar lost update, locks, double approval, double delivery e duplicidade de eventos. Usar constraints/locking quando apropriado.

## MVP
Entidades principais: account, user, conversation, message, message_review, review_suggestion.
Avaliar outbox quando a consistência banco + publicação de evento exigir atomicidade operacional.

## Handoff
Entregar modelo, migration, índices, impacto de performance, riscos de concorrência e estratégia de compatibilidade/rollback.
