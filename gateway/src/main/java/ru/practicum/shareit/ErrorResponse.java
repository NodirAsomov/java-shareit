package ru.practicum.shareit;


import java.util.HashMap;

public class ErrorResponse extends HashMap<String, String> {

    public ErrorResponse(String message) {
        put("error", message);
    }
}