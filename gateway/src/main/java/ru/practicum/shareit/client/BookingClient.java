package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.booking.BookingDto;

@Component
public class BookingClient extends BaseClient {

    private static final String PREFIX = "/bookings";

    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, BookingDto dto) {
        return post(PREFIX, userId, dto);
    }

    public ResponseEntity<Object> get(Long bookingId, Long userId) {
        return get(PREFIX + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> approve(Long bookingId, Long userId, Boolean approved) {
        return patch(PREFIX + "/" + bookingId + "?approved=" + approved, userId, null);
    }
}