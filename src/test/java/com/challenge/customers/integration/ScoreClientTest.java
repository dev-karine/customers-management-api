package com.challenge.customers.integration;

import com.challenge.customers.dto.ScoreResponse;
import com.challenge.customers.exception.ScoreServiceException;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScoreClientTest {
    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) server.stop(0);
    }

    @Test
    void shouldCallExternalScoreService() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/scores/12345678901", exchange -> {
            byte[] body = "{\"cpf\":\"12345678901\",\"score\":750,\"classification\":\"LOW_RISK\"}"
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        ScoreClient client = client(server.getAddress().getPort(), Duration.ofSeconds(1));
        ScoreResponse response = client.getScore("12345678901");

        assertThat(response.score()).isEqualTo(750);
        assertThat(response.classification()).isEqualTo("LOW_RISK");
    }

    @Test
    void shouldMapUnexpectedResponseToBadGateway() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/scores/12345678901", exchange -> {
            byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        ScoreClient client = client(server.getAddress().getPort(), Duration.ofSeconds(1));
        assertThatThrownBy(() -> client.getScore("12345678901"))
                .isInstanceOf(ScoreServiceException.class)
                .hasMessageContaining("unexpected response");
    }

    private ScoreClient client(int port, Duration readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(1));
        factory.setReadTimeout(readTimeout);
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .requestFactory(factory)
                .build();
        return new ScoreClient(restClient);
    }
}
