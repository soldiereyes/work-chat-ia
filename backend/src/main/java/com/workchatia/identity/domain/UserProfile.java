package com.workchatia.identity.domain;

import java.util.UUID;

public record UserProfile(UUID id, UUID accountId, String email, String displayName) {}
