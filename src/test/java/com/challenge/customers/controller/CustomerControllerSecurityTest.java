package com.challenge.customers.controller;

import com.challenge.customers.dto.CustomerResponse;
import com.challenge.customers.entity.CustomerStatus;
import com.challenge.customers.exception.GlobalExceptionHandler;
import com.challenge.customers.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import({com.challenge.customers.config.SecurityConfig.class, GlobalExceptionHandler.class})
class CustomerControllerSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean CustomerService service;

    @Test
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanRead() throws Exception {
        when(service.findAll(null)).thenReturn(List.of(
                new CustomerResponse(1L, "Joao", "12345678901", "joao@email.com", CustomerStatus.ACTIVE)));

        mvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreate() throws Exception {
        mvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Joao","cpf":"12345678901","email":"joao@email.com","status":"ACTIVE"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreate() throws Exception {
        when(service.create(org.mockito.ArgumentMatchers.any())).thenReturn(
                new CustomerResponse(1L, "Joao", "12345678901", "joao@email.com", CustomerStatus.ACTIVE));

        mvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Joao","cpf":"12345678901","email":"joao@email.com","status":"ACTIVE"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/customers/1"));
    }
}
