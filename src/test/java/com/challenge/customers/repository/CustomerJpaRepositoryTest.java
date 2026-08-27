package com.challenge.customers.repository;

import com.challenge.customers.entity.Customer;
import com.challenge.customers.entity.CustomerStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerJpaRepositoryTest {
    @Autowired CustomerRepository repository;

    @Test
    void shouldFindByStatusUsingNativeQuery() {
        Customer active = customer("Ana", "11111111111", CustomerStatus.ACTIVE);
        Customer inactive = customer("Bia", "22222222222", CustomerStatus.INACTIVE);
        repository.save(active);
        repository.save(inactive);

        assertThat(repository.findByStatusNative(CustomerStatus.ACTIVE))
                .extracting(Customer::getCpf)
                .containsExactly("11111111111");
    }

    private Customer customer(String name, String cpf, CustomerStatus status) {
        Customer c = new Customer();
        c.setName(name);
        c.setCpf(cpf);
        c.setEmail(name.toLowerCase() + "@email.com");
        c.setStatus(status);
        return c;
    }
}
