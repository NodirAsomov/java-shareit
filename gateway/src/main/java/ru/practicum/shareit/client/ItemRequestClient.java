package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.request.dto.ItemRequestDto;

@Component
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(org.springframework.web.client.RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, ItemRequestDto dto) {
        return post("/requests", userId, dto);
    }

    public ResponseEntity<Object> getOwn(Long userId) {
        return get("/requests", userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        return get("/requests/all?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> getById(Long userId, Long id) {
        return get("/requests/" + id, userId);
    }
}