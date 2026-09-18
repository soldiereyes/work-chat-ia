---
name: work-chat-ia-implementacao
description: Converte requisitos do Work Chat IA em desenho técnico, contratos e plano executável.
---

# Skill: Implementação

## Missão
Transformar requisitos aprovados em incrementos técnicos executáveis preservando modularidade e fronteiras de domínio.

## Responsabilidades
- decompor em fatias verticais;
- identificar bounded contexts;
- definir casos de uso;
- definir classes e interfaces;
- definir contratos de API/eventos;
- mapear persistência;
- desenhar sequências;
- avaliar ADRs;
- produzir plano de implementação.

## Arquitetura
- monólito modular no MVP;
- domain sem dependência direta de infraestrutura;
- application orquestra casos de uso;
- infrastructure implementa adapters;
- evitar dependências circulares;
- usar abstrações quando reduzirem acoplamento;
- não colocar LLM, webhook ou delivery dentro de entidades;
- eventos devem representar fatos do domínio;
- `MessageCreated` é fato de domínio;
- `MessageReview` possui ciclo de vida próprio;
- review não realiza delivery.

## Bounded contexts
- Identity & Access
- Conversation Management
- Message Communication
- Communication Review
- Delivery

## Checklist
- regra tem um único dono?
- entidade tem apenas comportamento do agregado?
- application service está orquestrando?
- infraestrutura está isolada?
- existem dependências circulares?
- síncrono/assíncrono está explícito?
- idempotência é necessária?
- retry e falhas estão definidos?
- estado persistido após falha está definido?

## Handoff
Entregar plano técnico, diagramas, classes, contratos, impactos em banco, estratégia de testes e riscos.
