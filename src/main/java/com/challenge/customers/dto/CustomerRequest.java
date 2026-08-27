package com.challenge.customers.dto;

import com.challenge.customers.entity.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "name is required")
        @Size(max = 120, message = "name must have at most 120 characters")
        String name,

        @NotBlank(message = "cpf is required")
        @Pattern(regexp = "\\d{11}", message = "cpf must contain exactly 11 digits")
        String cpf,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 160, message = "email must have at most 160 characters")
        String email,

        @NotNull(message = "status is required")
        CustomerStatus status
) {}
