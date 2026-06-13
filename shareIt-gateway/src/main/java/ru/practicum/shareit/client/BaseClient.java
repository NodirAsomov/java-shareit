package ru.practicum.shareit.client;


import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

public class BaseClient {

    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return request(HttpMethod.GET, path, userId, null);
    }

    protected ResponseEntity<Object> post(String path, Long userId, @Nullable Object body) {
        return request(HttpMethod.POST, path, userId, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, @Nullable Object body) {
        return request(HttpMethod.PATCH, path, userId, body);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return request(HttpMethod.DELETE, path, userId, null);
    }

    private ResponseEntity<Object> request(
            HttpMethod method,
            String path,
            Long userId,
            @Nullable Object body
    ) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Sharer-User-Id", String.valueOf(userId));

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        return rest.exchange(path, method, entity, Object.class);
    }
}