package ru.practicum.clients;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.dto.RequestDto;

@Component
public class RequestClient {

    private final BaseClient baseClient;

    public RequestClient(BaseClient baseClient) {
        this.baseClient = baseClient;
    }

    public ResponseEntity<Object> create(Long userId, RequestDto dto) {
        return baseClient.post("/requests", dto, userId);
    }

    public ResponseEntity<Object> getOwn(Long userId) {
        return baseClient.get("/requests", userId);
    }

    public ResponseEntity<Object> getAll(Long userId) {
        return baseClient.get("/requests/all", userId);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return baseClient.get("/requests/" + requestId, userId);
    }
}