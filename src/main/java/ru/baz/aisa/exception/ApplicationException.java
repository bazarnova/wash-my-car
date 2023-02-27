package ru.baz.aisa.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
}
