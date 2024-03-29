package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private static String stackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        log.error(String.format("Conflict error: %s", e.getMessage()));
        log.error(String.format("stackTrace: %s", stackTrace(e)));
        return new ErrorResponse(String.format("Conflict error: %s", e.getMessage()),
                String.format("stackTrace: %s", stackTrace(e)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(String.format("DataIntegrityViolation error: %s", e.getMessage()));
        log.error(String.format("stackTrace: %s", stackTrace(e)));
        return new ErrorResponse(String.format("DataIntegrityViolation error: %s", e.getMessage()),
                String.format("stackTrace: %s", stackTrace(e)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.error(String.format("Validation error: %s", e.getMessage()));
        log.error(String.format("stackTrace: %s", stackTrace(e)));
        return new ErrorResponse(String.format("Validation error: %s", e.getMessage()),
                String.format("stackTrace: %s", stackTrace(e)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.error(String.format("Validation error: %s", e.getMessage()));
        log.error(String.format("stackTrace: %s", stackTrace(e)));
        return new ErrorResponse(String.format("Not found error: %s", e.getMessage()),
                String.format("stackTrace: %s", stackTrace(e)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(String.format("Validation error: %s", e.getFieldError().getDefaultMessage()));
        log.error(String.format("stackTrace: %s", stackTrace(e)));
        return new ErrorResponse(String.format("MethodArgumentNotValid error: %s",
                e.getFieldError().getDefaultMessage()),
                String.format("stackTrace: %s", stackTrace(e)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error(String.format("Validation error: %s", e.getMessage()));
        log.error(String.format("stackTrace: %s", stackTrace(e)));
        return new ErrorResponse(String.format("Internal error: %s", e.getMessage()),
                String.format("stackTrace: %s", stackTrace(e)));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnsupportedException(UnsupportedException e) {
        log.error(String.format("UnsupportedException: %s", e.getMessage()));
        log.error(String.format("stackTrace: %s", stackTrace(e)));
        return new ErrorResponse(String.format("%s", e.getMessage()),
                String.format("stackTrace: %s", stackTrace(e)));
    }
}
