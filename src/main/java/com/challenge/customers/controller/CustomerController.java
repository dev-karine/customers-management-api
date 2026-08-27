package com.challenge.customers.controller;

import com.challenge.customers.dto.CustomerRequest;
import com.challenge.customers.dto.CustomerResponse;
import com.challenge.customers.dto.ScoreResponse;
import com.challenge.customers.entity.CustomerStatus;
import com.challenge.customers.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
@Validated
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/customers/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<CustomerResponse> findAll(@RequestParam(required = false) CustomerStatus status) {
        return service.findAll(status);
    }

    @GetMapping("/search")
    public List<CustomerResponse> search(@RequestParam @NotBlank(message = "name must not be blank") String name) {
        return service.searchByName(name);
    }

    @GetMapping("/{id}/score")
    public ScoreResponse score(@PathVariable Long id) {
        return service.getScore(id);
    }
}
