package com.challenge.customers.service;

import com.challenge.customers.dto.CustomerRequest;
import com.challenge.customers.entity.Customer;
import com.challenge.customers.entity.CustomerStatus;
import com.challenge.customers.exception.CustomerNotFoundException;
import com.challenge.customers.exception.DuplicateCpfException;
import com.challenge.customers.integration.ScoreClient;
import com.challenge.customers.repository.CustomerJdbcRepository;
import com.challenge.customers.repository.CustomerRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
    private final CustomerRepository repository = mock(CustomerRepository.class);
    private final CustomerJdbcRepository jdbcRepository = mock(CustomerJdbcRepository.class);
    private final ScoreClient scoreClient = mock(ScoreClient.class);
    private final CustomerService service = new CustomerService(repository, jdbcRepository, scoreClient);

    @Test
    void shouldRejectDuplicatedCpfOnCreate() {
        when(repository.existsByCpf("12345678901")).thenReturn(true);
        CustomerRequest request = new CustomerRequest("Joao", "12345678901", "joao@email.com", CustomerStatus.ACTIVE);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateCpfException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void shouldFailWhenUpdatingMissingCustomer() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        CustomerRequest request = new CustomerRequest("Joao", "12345678901", "joao@email.com", CustomerStatus.ACTIVE);

        assertThatThrownBy(() -> service.update(99L, request))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void shouldUseCustomerCpfWhenRequestingScore() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCpf("12345678901");
        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        service.getScore(1L);

        verify(scoreClient).getScore("12345678901");
    }
}
