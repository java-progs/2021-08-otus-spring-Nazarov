package ru.otus.homework.exception;

public class ViolationOfConstraintException extends RuntimeException {

    public ViolationOfConstraintException(String message) {
        super(message);
    }
}
