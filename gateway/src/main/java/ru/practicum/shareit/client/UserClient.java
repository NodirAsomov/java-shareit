package ru.practicum.shareit.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(RestTemplate restTemplate,
                      @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> create(Object body) {
        return post(API_PREFIX, null, body);
    }

    public ResponseEntity<Object> update(Long id, Object body) {
        return patch(API_PREFIX + "/" + id, null, body);
    }

    public ResponseEntity<Object> get(Long id) {
        return get(API_PREFIX + "/" + id, null);
    }

    public ResponseEntity<Object> delete(Long id) {
        return delete(API_PREFIX + "/" + id, null);
    }
}