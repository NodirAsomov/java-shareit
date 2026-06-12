package ru.practicum.clients;


import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


public class BaseClient {

    private final RestTemplate rest;
    private final String serverUrl;

    public BaseClient(RestTemplate rest, String serverUrl) {
        this.rest = rest;
        this.serverUrl = serverUrl;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return request(HttpMethod.GET, path, userId, null);
    }

    protected ResponseEntity<Object> post(String path, Object body, Long userId) {
        return request(HttpMethod.POST, path, userId, body);
    }

    protected ResponseEntity<Object> patch(String path, Object body, Long userId) {
        return request(HttpMethod.PATCH, path, userId, body);
    }

    private ResponseEntity<Object> request(
            HttpMethod method,
            String path,
            Long userId,
            Object body) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (userId != null) {
            headers.add("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        return rest.exchange(
                serverUrl + path,
                method,
                entity,
                Object.class
        );
    }
}