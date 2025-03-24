package com.peter.authnservicespringboot.domain.dto;

import java.time.ZonedDateTime;

public record UserRegistrationResponse(
        Long id,
        String email,
        ZonedDateTime created_at
) {}
