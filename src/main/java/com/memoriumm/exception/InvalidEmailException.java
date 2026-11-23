package com.memoriumm.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super("E-mail inválido");
    }
}
