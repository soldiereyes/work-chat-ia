# Proposta de valor

## Problema

Profissionais que atendem clientes por chat frequentemente enviam mensagens que:

- violam políticas internas de comunicação;
- usam linguagem inadequada ou ambígua;
- não seguem padrões de qualidade da empresa.

Revisar manualmente cada mensagem não escala. Delegar 100% à IA remove a autonomia do profissional.

## Solução

Um fluxo de **revisão colaborativa**:

| Etapa | Responsável |
|-------|-------------|
| Escrever a mensagem | Profissional |
| Avaliar regras e linguagem | Sistema (`CommunicationPolicy` + `AIReviewService`) |
| Decidir o que enviar | Profissional |
| Entregar ao cliente | Sistema (`MessageDeliveryService`) |

## Diferenciação

- **Não é** um chatbot que responde pelo profissional.
- **Não é** um bloqueio automático sem contexto.
- **É** um assistente de revisão que produz `ReviewSuggestion` e deixa a decisão final com o `Professional`.

## Resultados esperados

- Redução de mensagens inadequadas antes do envio.
- Padronização da comunicação sem perder o tom humano.
- Rastreabilidade de revisões (`MessageReview`) para auditoria e melhoria contínua das políticas.
