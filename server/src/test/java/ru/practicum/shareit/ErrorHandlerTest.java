package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.*;

import static org.junit.jupiter.api.Assertions.*;


class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void notFound() {
        ErrorResponse resp = handler.notFound(
                new NotFoundException("not found")
        );

        assertEquals("not found", resp.get("error"));
    }

    @Test
    void validation() {
        ErrorResponse resp = handler.badRequest(
                new ValidationException("bad request")
        );

        assertEquals("bad request", resp.get("error"));
    }

    @Test
    void forbidden() {
        ErrorResponse resp = handler.forbidden(
                new AccessException("forbidden")
        );

        assertEquals("forbidden", resp.get("error"));
    }

    @Test
    void conflict() {
        ErrorResponse resp = handler.conflict(
                new ConflictException("conflict")
        );

        assertEquals("conflict", resp.get("error"));
    }

    @Test
    void db_error() {
        ErrorResponse resp = handler.handleDb(
                new DataIntegrityViolationException("db error")
        );

        assertEquals("Constraint violation", resp.get("error"));
    }

    @Test
    void illegal_argument() {
        ErrorResponse resp = handler.badRequestIllegal(
                new IllegalArgumentException("illegal")
        );

        assertEquals("illegal", resp.get("error"));
    }

    @Test
    void generic() {
        ErrorResponse resp = handler.error(
                new RuntimeException("unexpected")
        );

        assertEquals("unexpected", resp.get("error"));
    }
}