package com.workchatia.shared.domain.event;

public interface OutboxRepository {

    void append(DomainEvent event);
}
