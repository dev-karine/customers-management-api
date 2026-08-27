package com.challenge.customers.repository;

import com.challenge.customers.entity.Customer;
import com.challenge.customers.entity.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Long id);

    // Requisito do desafio: pelo menos uma consulta usando Native Query.
    @Query(value = "SELECT * FROM customers WHERE status = :status ORDER BY id", nativeQuery = true)
    List<Customer> findByStatusNative(@Param("status") String status);

    default List<Customer> findByStatusNative(CustomerStatus status) {
        return findByStatusNative(status.name());
    }
}
