package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Component
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(RestTemplate restTemplate,
                         @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate);

        this.rest.setUriTemplateHandler(
                new DefaultUriBuilderFactory(serverUrl + API_PREFIX)
        );
    }

    public ResponseEntity<Object> getBookings(long userId,
                                              BookingState state,
                                              int from,
                                              int size) {
        return get("?state=" + state.name() + "&from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> bookItem(long userId,
                                           Object body) {
        return post("", userId, body);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }
}