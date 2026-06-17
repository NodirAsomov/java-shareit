package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void notFoundHandler() {
        NotFoundException ex = new NotFoundException("not found");

        var result = handler.notFound(ex);

        assertEquals("not found", result.get("error"));
    }

    @Test
    void badRequestHandler() {
        ValidationException ex = new ValidationException("bad request");

        var result = handler.badRequest(ex);

        assertEquals("bad request", result.get("error"));
    }

    @Test
    void forbiddenHandler() {
        AccessException ex = new AccessException("forbidden");

        var result = handler.forbidden(ex);

        assertEquals("forbidden", result.get("error"));
    }

    @Test
    void conflictHandler() {
        ConflictException ex = new ConflictException("conflict");

        var result = handler.conflict(ex);

        assertEquals("conflict", result.get("error"));
    }
}