package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public abstract class BaseClient {

    protected final RestTemplate rest;
    private final String serverUrl;

    public BaseClient(RestTemplate rest, String serverUrl) {
        this.rest = rest;
        this.serverUrl = serverUrl.endsWith("/")
                ? serverUrl.substring(0, serverUrl.length() - 1)
                : serverUrl;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return exchange(path, HttpMethod.GET, new HttpEntity<>(defaultHeaders(userId)));
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return exchange(path, HttpMethod.POST, new HttpEntity<>(body, defaultHeaders(userId)));
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return exchange(path, HttpMethod.PATCH, new HttpEntity<>(body, defaultHeaders(userId)));
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return exchange(path, HttpMethod.DELETE, new HttpEntity<>(defaultHeaders(userId)));
    }

    private ResponseEntity<Object> exchange(String path, HttpMethod method, HttpEntity<?> requestEntity) {
        try {
            return rest.exchange(serverUrl + path, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
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
