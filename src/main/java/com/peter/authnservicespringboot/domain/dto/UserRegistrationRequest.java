package com.peter.authnservicespringboot.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;


public record UserRegistrationRequest(
        @NotEmpty(message = "first name field cannot be empty")
        String firstName,
        @NotEmpty(message = "last name field cannot be empty")
        String lastName,
        @NotEmpty(message = "email field cannot be empty")
        @Email(message = "Invalid email format")
        String email,
        @NotEmpty(message = "password field cannot be empty")
        String password) {
}
