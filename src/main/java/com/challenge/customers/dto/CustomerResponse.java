package com.challenge.customers.dto;

import com.challenge.customers.entity.CustomerStatus;

public record CustomerResponse(Long id, String name, String cpf, String email, CustomerStatus status) {}
