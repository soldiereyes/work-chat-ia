---
name: work-chat-ia-desenvolvedores
description: Implementa backend Java/Spring, frontend React/TypeScript e realiza revisão técnica no Work Chat IA.
---

# Skill: Desenvolvedores

## Missão
Implementar código de produção com comportamento correto, baixo acoplamento e testes adequados.

## Stack
Backend: Java 21, Spring Boot, Spring Web, Data JPA/JDBC, Security, PostgreSQL, Redis, WebSocket, Flyway, JUnit, Mockito.
Frontend: React, TypeScript, Vite e WebSocket.

## Regras backend
- Controller trata protocolo e delega.
- Entidades não conhecem HTTP, JPA ou LLM.
- Repositórios de domínio são abstrações; adapters ficam na infraestrutura.
- Regras de estado ficam no domínio ou componente responsável.
- Não colocar regra de negócio em DTO.
- Erros de domínio têm tradução HTTP consistente.
- Integração LLM usa `LLMProvider`.
- Integração de canal usa `ChannelGateway`.
- Async possui rastreabilidade e política de retry quando aplicável.

## Regras frontend
- componentes não são fonte única das regras de domínio;
- backend continua garantindo invariantes;
- estados loading/error/empty/success/eventual consistency são explícitos;
- WebSocket não substitui estado persistido;
- mudanças de contrato atualizam tipos e testes.

## Estados conhecidos
Message: `DRAFT → PENDING_REVIEW → APPROVED → SENDING → SENT → DELIVERED`, com estados de erro/bloqueio conforme domínio.
MessageReview: `PENDING → IN_PROGRESS → COMPLETED` ou `FAILED`.
ReviewResult: `ALLOWED | SUGGESTION | BLOCKED`.

## Testes
Adicionar testes proporcionais ao risco: unidade, aplicação, integração, contrato e frontend conforme aplicabilidade.

## Code review
Verificar bounded context, dependências, transições, erros, concorrência/idempotência, segurança, observabilidade, testes, migrations e documentação.

## Nunca
- copiar regras entre módulos;
- criar service genérico para tudo;
- chamar LLM diretamente de Controller;
- usar Redis como banco principal;
- ocultar mudança de regra em refactor;
- ignorar falhas assíncronas;
- introduzir contexto global implícito sem decisão arquitetural.

## Handoff
Listar arquivos, comportamento, testes, migration, riscos e observações para Qualidade/DevOps.
