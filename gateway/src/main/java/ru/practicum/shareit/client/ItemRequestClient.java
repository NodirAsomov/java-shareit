package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.request.RequestCreateDto;

@Component
public class ItemRequestClient extends BaseClient {

    private static final String PREFIX = "/requests";

    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }


    public ResponseEntity<Object> createRequest(Long userId, RequestCreateDto dto) {
        return post(PREFIX, userId, dto);
    }


    public ResponseEntity<Object> getOwnRequests(Long userId) {
        return get(PREFIX, userId);
    }


    public ResponseEntity<Object> getAllRequests(Long userId, int from, int size) {
        return get(PREFIX + "/all?from=" + from + "&size=" + size, userId);
    }


    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get(PREFIX + "/" + requestId, userId);
    }
}