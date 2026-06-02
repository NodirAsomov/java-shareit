package ru.practicum.shareit.booking.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;

import java.util.*;

@Repository
public class InMemoryBookingStorage implements BookingStorage {

    private final Map<Long, Booking> storage = new HashMap<>();
    private long id = 1;

    @Override
    public Booking add(Booking booking) {
        booking.setId(id++);
        storage.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking update(Booking booking) {
        storage.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking getById(Long id) {
        return storage.get(id);
    }

    @Override
    public List<Booking> getAll() {
        return new ArrayList<>(storage.values());
    }
}