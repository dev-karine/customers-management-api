package com.challenge.customers.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "score-service")
public record ScoreClientProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {}
