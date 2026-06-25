package ru.practicum.shareit.booking.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequestDto {

    @NotBlank(message = "Description must not be empty")
    private String description;
}