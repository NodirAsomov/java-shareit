package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.user.UserDto;

@Component
public class UserClient extends BaseClient {

    private static final String PREFIX = "/users";

    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(UserDto dto) {
        return post(PREFIX, null, dto);
    }

    public ResponseEntity<Object> get(Long id, Long userId) {
        return get(PREFIX + "/" + id, userId);
    }

    public ResponseEntity<Object> update(Long id, UserDto dto, Long userId) {
        return patch(PREFIX + "/" + id, userId, dto);
    }

    public ResponseEntity<Object> delete(Long id, Long userId) {
        return delete(PREFIX + "/" + id, userId);
    }
}