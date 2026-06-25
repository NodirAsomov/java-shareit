package ru.practicum.shareit;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation error");

        return new ErrorResponse(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraint(ConstraintViolationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }


    @ExceptionHandler({
            jakarta.persistence.EntityNotFoundException.class,
            java.util.NoSuchElementException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }


    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflict(IllegalStateException e) {
        return new ErrorResponse(e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse fallback(Exception e) {
        return new ErrorResponse("Internal error: " + e.getMessage());
    }
}