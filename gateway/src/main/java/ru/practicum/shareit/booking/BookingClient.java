package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Component
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(RestTemplate restTemplate,
                         @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> getBookings(long userId,
                                              BookingState state,
                                              int from,
                                              int size) {
        return get(API_PREFIX + "?state=" + state.name()
                + "&from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> bookItem(long userId, Object body) {
        return post(API_PREFIX, userId, body);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId);
    }
}