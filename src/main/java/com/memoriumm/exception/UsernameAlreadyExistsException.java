package com.memoriumm.exception;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(){
        super("Este nome de usuário já está cadastrado");
    }
}
