package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(RestTemplate restTemplate,
                      @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Long userId, Object body) {
        return post(API_PREFIX, userId, body);
    }

    public ResponseEntity<Object> get(Long userId, Long itemId) {
        return get(API_PREFIX + "/" + itemId, userId);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, Object body) {
        return patch(API_PREFIX + "/" + itemId, userId, body);
    }

    public ResponseEntity<Object> getAll(Long userId) {
        return get(API_PREFIX, userId);
    }

    public ResponseEntity<Object> search(Long userId, String text) {
        return get(API_PREFIX + "/search?text="
                + URLEncoder.encode(text, StandardCharsets.UTF_8), userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, Object body) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, body);
    }
}
