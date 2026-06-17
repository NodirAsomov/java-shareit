package ru.practicum.shareit.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {

        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, Object body) {
        return post("", userId, body);
    }

    public ResponseEntity<Object> getOwn(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        return get("/all?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}