package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {

    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return request(HttpMethod.GET, path, userId, null, null);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return request(HttpMethod.POST, path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return request(HttpMethod.PATCH, path, userId, null, body);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return request(HttpMethod.DELETE, path, userId, null, null);
    }

    private <T> ResponseEntity<Object> request(HttpMethod method,
                                               String path,
                                               Long userId,
                                               Map<String, Object> params,
                                               T body) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<T> entity = new HttpEntity<>(body, headers);

        try {
            return rest.exchange(path, method, entity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsByteArray());
        }
    }
}