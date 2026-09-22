# MVP-01 — Validação Qualidade (Identity & Access)

Card: MVP-01  
Requisitos: REQ-F-001, REQ-NF-005  

## Veredito

**PASS** quando `docker compose up -d postgres` e `mvn clean test` executam com sucesso (PostgreSQL em `localhost:55432`).

## Correção Q-101 (falso verde)

O relatório anterior citava `AuthenticationControllerIT`, que **não** era descoberto pelo Surefire (`*IT` fora do padrão `*Test`). A classe foi renomeada para `AuthenticationControllerTest`. O veredito PASS anterior sem `Tests run > 0` foi **invalidado** e corrigido.

## Evidência reproduzível

```bash
cd backend && mvn clean test
```

Pré-requisito: `docker compose up -d postgres` (porta **55432**).

Esperado:

```text
Tests run: 13, Failures: 0, Errors: 0
```

| Classe | Testes |
|--------|--------|
| `com.workchatia.identity.AuthenticationControllerTest` | 11 |
| `com.workchatia.identity.infrastructure.security.JwtServiceTest` | 2 |

## Mapeamento aceite → teste

| Aceite | Cenário | Método |
|--------|---------|--------|
| AC-001 | Login válido | `loginWithValidCredentialsReturnsToken` |
| AC-001 | Login inválido | `loginWithInvalidCredentialsReturns401` |
| AC-001 | Token + probe envio | `sendMessageWithPermissionReturns204` |
| AC-002 | Sem SEND_MESSAGE | `sendMessageWithoutPermissionReturns403` |
| AC-020 | Mesmo account | `sameAccountUserAccessReturns200` |
| AC-020 | Cross-account | `crossAccountUserAccessReturns403` |
| — | Sem token | `protectedRouteWithoutTokenReturns401` |
| — | Token inválido | `protectedRouteWithInvalidTokenReturns401` |
| — | Token expirado (HTTP) | `protectedRouteWithExpiredTokenReturns401` |
| — | Validação login | `loginWithInvalidEmailFormatReturns400` |
| — | GET /me | `meEndpointReturnsCurrentUser` |
| — | JWT expirado (unit) | `JwtServiceTest.parseTokenRejectsExpiredJwt` |
| — | Round-trip JWT | `JwtServiceTest.generateAndParseRoundTrip` |

## Revalidação (2026-09-21)

- Q-101: renomeado para `AuthenticationControllerTest`
- Q-103: `JwtServiceTest` + IT token expirado
- Q-104: CI executa `mvn test` com guard contra 0 testes

## Revalidação (2026-09-22)

- Q-105: removido Testcontainers dos ITs; JDBC fixo em `localhost:55432` (+ `TEST_JDBC_URL` no CI)
- Evidência: `mvn -B clean test` → **13** testes, **0** falhas; guard do `backend-ci.yml` reproduzido localmente no log
- Dependência `testcontainers` ausente do `backend/pom.xml` (grep sem ocorrências)

## Riscos residuais

| Risco | Mitigação |
|-------|-----------|
| AC-020 com Conversation | MVP-02 |
| Probe POST /messages | MVP-03 |
| Seed só dev/test | `db/migration-dev` |
| Postgres do compose parado | `mvn test` falha na conexão JDBC — subir `docker compose up -d postgres` |

## Fora do escopo

Conversation, Message, revisão, delivery, frontend, áudio (MVP-09).
