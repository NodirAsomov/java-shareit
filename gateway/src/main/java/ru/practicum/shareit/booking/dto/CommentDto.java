package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDto {

    @NotBlank
    private String text;
}
