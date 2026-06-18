package ru.practicum.shareit.client;


import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public abstract class BaseClient {

    protected final RestTemplate rest;
    private final String serverUrl;

    public BaseClient(RestTemplate rest, String serverUrl) {
        this.rest = rest;
        this.serverUrl = serverUrl;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders(userId));
        return rest.exchange(serverUrl + path, HttpMethod.GET, entity, Object.class);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, defaultHeaders(userId));
        return rest.exchange(serverUrl + path, HttpMethod.POST, entity, Object.class);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, defaultHeaders(userId));
        return rest.exchange(serverUrl + path, HttpMethod.PATCH, entity, Object.class);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders(userId));
        return rest.exchange(serverUrl + path, HttpMethod.DELETE, entity, Object.class);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        return headers;
    }
}