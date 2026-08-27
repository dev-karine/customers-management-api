package com.challenge.customers.integration;

import com.challenge.customers.dto.ScoreResponse;
import com.challenge.customers.exception.ScoreServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;

@Component
public class ScoreClient {
    private final RestClient restClient;

    public ScoreClient(RestClient scoreRestClient) {
        this.restClient = scoreRestClient;
    }

    public ScoreResponse getScore(String cpf) {
        try {
            ScoreResponse response = restClient.get()
                    .uri("/scores/{cpf}", cpf)
                    .retrieve()
                    .body(ScoreResponse.class);

            if (response == null || response.cpf() == null || response.score() == null || response.classification() == null) {
                throw new ScoreServiceException(HttpStatus.BAD_GATEWAY,
                        "Score service returned an unexpected response");
            }
            return response;
        } catch (ScoreServiceException ex) {
            throw ex;
        } catch (RestClientResponseException ex) {
            HttpStatus status = ex.getStatusCode().is5xxServerError()
                    ? HttpStatus.SERVICE_UNAVAILABLE
                    : HttpStatus.BAD_GATEWAY;
            throw new ScoreServiceException(status,
                    "Score service returned HTTP " + ex.getStatusCode().value(), ex);
        } catch (ResourceAccessException ex) {
            if (hasTimeoutCause(ex)) {
                throw new ScoreServiceException(HttpStatus.GATEWAY_TIMEOUT,
                        "Score service response timed out", ex);
            }
            throw new ScoreServiceException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Score service is unavailable", ex);
        } catch (RestClientException ex) {
            throw new ScoreServiceException(HttpStatus.BAD_GATEWAY,
                    "Failed to communicate with score service", ex);
        }
    }

    private boolean hasTimeoutCause(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
