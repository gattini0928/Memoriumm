package com.memoriumm.exception;

public class InvalidCreatePasswordException extends RuntimeException {
  public InvalidCreatePasswordException(String message) {
    super(message);
  }
}
