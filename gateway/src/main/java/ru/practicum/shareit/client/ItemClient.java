package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.item.ItemDto;

@Component
public class ItemClient extends BaseClient {

    private static final String PREFIX = "/items";

    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, ItemDto dto) {
        return post(PREFIX, userId, dto);
    }

    public ResponseEntity<Object> get(Long itemId, Long userId) {
        return get(PREFIX + "/" + itemId, userId);
    }

    public ResponseEntity<Object> getAll(Long userId) {
        return get(PREFIX, userId);
    }
}