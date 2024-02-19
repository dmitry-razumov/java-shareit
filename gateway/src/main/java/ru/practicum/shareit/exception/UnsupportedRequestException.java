package ru.practicum.shareit.exception;

public class UnsupportedRequestException extends RuntimeException {
    public UnsupportedRequestException(String message) {
        super(message);
    }
}
