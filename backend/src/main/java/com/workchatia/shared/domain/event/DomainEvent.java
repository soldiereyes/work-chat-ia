package com.workchatia.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID eventId();

    String eventType();

    int version();

    Instant occurredAt();
}
