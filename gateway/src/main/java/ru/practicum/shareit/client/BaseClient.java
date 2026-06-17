package ru.practicum.shareit.client;


import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public abstract class BaseClient {

    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        HttpHeaders headers = defaultHeaders(userId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return rest.exchange(path, HttpMethod.GET, entity, Object.class);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, defaultHeaders(userId));

        return rest.exchange(path, HttpMethod.POST, entity, Object.class);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, defaultHeaders(userId));

        return rest.exchange(path, HttpMethod.PATCH, entity, Object.class);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders(userId));

        return rest.exchange(path, HttpMethod.DELETE, entity, Object.class);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        return headers;
    }
}