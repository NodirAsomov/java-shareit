package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(RestTemplate restTemplate,
                      @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate);

        this.rest.setUriTemplateHandler(
                new DefaultUriBuilderFactory(serverUrl + API_PREFIX)
        );
    }

    public ResponseEntity<Object> create(Long userId, Object body) {
        return post("", userId, body);
    }

    public ResponseEntity<Object> get(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }
}