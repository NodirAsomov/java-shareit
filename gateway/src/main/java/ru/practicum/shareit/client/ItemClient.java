package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.item.dto.ItemCreateDto;

@Component
public class ItemClient extends BaseClient {

    public ItemClient(org.springframework.web.client.RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, ItemCreateDto dto) {
        return post("/items", userId, dto);
    }

    public ResponseEntity<Object> get(Long userId, Long id) {
        return get("/items/" + id, userId);
    }

    public ResponseEntity<Object> getAll(Long userId) {
        return get("/items", userId);
    }
}