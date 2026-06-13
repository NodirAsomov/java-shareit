package ru.practicum.shareit.dto.booking;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class BookingDto {

    private Long id;

    @NotNull
    private Long itemId;

    private Long bookerId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private String status;

    public BookingDto() {
    }

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getBookerId() {
        return bookerId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setBookerId(Long bookerId) {
        this.bookerId = bookerId;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}