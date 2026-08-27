package com.challenge.customers.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityUsersProperties(Credential user, Credential admin) {
    public record Credential(String username, String password) {}
}
