package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public RequestClient(RestTemplate restTemplate,
                         @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Long userId, Object body) {
        return post(API_PREFIX, userId, body);
    }

    public ResponseEntity<Object> getOwn(Long userId) {
        return get(API_PREFIX, userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        return get(API_PREFIX + "/all?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get(API_PREFIX + "/" + requestId, userId);
    }
}