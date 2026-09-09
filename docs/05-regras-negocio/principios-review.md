# Princípios de revisão

## 1. A IA sugere; o profissional decide

O sistema **nunca** envia mensagem automaticamente após revisão negativa ou com sugestões pendentes, sem ação explícita do `Professional`.

## 2. Review é um bounded context isolado

`MessageReviewService`:

- ✅ avalia conteúdo
- ✅ produz `MessageReview` e `ReviewResult`
- ❌ não envia mensagem
- ❌ não gerencia usuário
- ❌ não gerencia conversa

## 3. Sugestão ≠ Bloqueio

| `ReviewResult` | Comportamento na UI |
|----------------|---------------------|
| `ALLOWED` | Pode aprovar e enviar |
| `SUGGESTION` | Exibe sugestões; profissional pode editar e reenviar |
| `BLOCKED` | Impede envio; exibe motivo |

## 4. Revisão em duas camadas

1. **Determinística** — `CommunicationPolicy` (rápida, previsível)
2. **Semântica** — `AIReviewService` + `LLMProvider` (contexto e linguagem)

A IA só é acionada quando as regras determinísticas não resolvem ou quando o contexto exige análise de linguagem.

## 5. Nomenclatura do domínio

- Use `MessageReview`, `ReviewSuggestion`, `ReviewResult`
- **Não** use `AIValidator` como conceito central
- Eventos: `MessageCreated`, `ReviewCompleted`
