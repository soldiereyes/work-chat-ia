# Communication Policy

`CommunicationPolicy` encapsula **regras determinísticas** de comunicação, executadas antes ou em conjunto com a revisão por IA.

## Responsabilidade

```text
CommunicationPolicy.evaluate(content) → PolicyEvaluation
```

Retorna violações (`PolicyViolation`) que podem resultar em:

- `ReviewResult.SUGGESTION` — orientação de melhoria
- `ReviewResult.BLOCKED` — impedimento de envio

## Exemplos de regras (MVP)

| Regra | Tipo de resultado |
|-------|-------------------|
| Palavras proibidas | `BLOCKED` |
| Mensagem vazia ou só espaços | `BLOCKED` |
| Excesso de caracteres | `SUGGESTION` |
| Ausência de saudação em primeiro contato | `SUGGESTION` |
| Dados sensíveis (CPF, cartão) | `BLOCKED` |

## Implementação sugerida

```text
CommunicationPolicy (interface)
    ├── RegexCommunicationPolicy
    ├── WordListCommunicationPolicy
    └── CompositeCommunicationPolicy
```

Regras configuráveis por `Account` em versões futuras. No MVP, podem ser estáticas ou em arquivo de configuração.

## Separação de `AIReviewService`

| Camada | Quando usar |
|--------|-------------|
| `CommunicationPolicy` | Regras explícitas, binárias, auditáveis |
| `AIReviewService` | Tom, clareza, adequação contextual |
