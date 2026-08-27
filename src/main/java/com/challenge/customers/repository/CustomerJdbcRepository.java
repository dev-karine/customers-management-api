package com.challenge.customers.repository;

import com.challenge.customers.entity.Customer;
import com.challenge.customers.entity.CustomerStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public CustomerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Customer> searchByName(String name) {
        String sql = """
                SELECT id, name, cpf, email, status
                  FROM customers
                 WHERE LOWER(name) LIKE LOWER(?)
                 ORDER BY name, id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("name"));
            customer.setCpf(rs.getString("cpf"));
            customer.setEmail(rs.getString("email"));
            customer.setStatus(CustomerStatus.valueOf(rs.getString("status")));
            return customer;
        }, "%" + name + "%");
    }
}
