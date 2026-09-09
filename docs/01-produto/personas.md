# Personas

## Professional (Profissional)

Usuário que atende clientes e envia mensagens pelo sistema.

**Objetivos:**
- Comunicar-se de forma clara e dentro das políticas da empresa.
- Receber sugestões rápidas sem perder autonomia.
- Aprovar e enviar mensagens com confiança.

**Interações principais:**
- Escrever e editar `Message`
- Interpretar `ReviewResult` e `ReviewSuggestion`
- Aprovar ou ajustar antes do envio

---

## Client (Cliente)

Destinatário final da mensagem no canal externo.

**No MVP:** não interage diretamente com o sistema de revisão. Recebe apenas mensagens já aprovadas pelo profissional.

---

## Account Administrator (futuro)

Responsável por configurar `CommunicationPolicy`, permissões e integrações.

**No MVP:** representado indiretamente por `Account`, `Role` e `Permission` no bounded context **Identity & Access**.
