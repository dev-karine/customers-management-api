package com.challenge.customers.service;

import com.challenge.customers.dto.CustomerRequest;
import com.challenge.customers.dto.CustomerResponse;
import com.challenge.customers.dto.ScoreResponse;
import com.challenge.customers.entity.Customer;
import com.challenge.customers.entity.CustomerStatus;
import com.challenge.customers.exception.CustomerNotFoundException;
import com.challenge.customers.exception.DuplicateCpfException;
import com.challenge.customers.integration.ScoreClient;
import com.challenge.customers.repository.CustomerJdbcRepository;
import com.challenge.customers.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerJdbcRepository jdbcRepository;
    private final ScoreClient scoreClient;

    public CustomerService(CustomerRepository repository, CustomerJdbcRepository jdbcRepository, ScoreClient scoreClient) {
        this.repository = repository;
        this.jdbcRepository = jdbcRepository;
        this.scoreClient = scoreClient;
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (repository.existsByCpf(request.cpf())) {
            throw new DuplicateCpfException(request.cpf());
        }

        Customer customer = new Customer();
        apply(customer, request);
        return toResponse(repository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findEntity(id);
        if (repository.existsByCpfAndIdNot(request.cpf(), id)) {
            throw new DuplicateCpfException(request.cpf());
        }
        apply(customer, request);
        return toResponse(repository.save(customer));
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = findEntity(id);
        repository.delete(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return toResponse(findEntity(id));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll(CustomerStatus status) {
        List<Customer> customers = status == null
                ? repository.findAll()
                : repository.findByStatusNative(status);
        return customers.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> searchByName(String name) {
        return jdbcRepository.searchByName(name.trim()).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ScoreResponse getScore(Long id) {
        Customer customer = findEntity(id);
        return scoreClient.getScore(customer.getCpf());
    }

    private Customer findEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    private void apply(Customer customer, CustomerRequest request) {
        customer.setName(request.name().trim());
        customer.setCpf(request.cpf());
        customer.setEmail(request.email().trim().toLowerCase());
        customer.setStatus(request.status());
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(), customer.getName(), customer.getCpf(), customer.getEmail(), customer.getStatus());
    }
}
