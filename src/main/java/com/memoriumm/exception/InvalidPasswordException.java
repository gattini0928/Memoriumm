package com.memoriumm.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Senha atual incorreta");
    }
}
