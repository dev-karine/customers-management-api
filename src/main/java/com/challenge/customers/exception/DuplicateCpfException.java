package com.challenge.customers.exception;

public class DuplicateCpfException extends RuntimeException {
    public DuplicateCpfException(String cpf) {
        super("CPF already registered: " + cpf);
    }
}
