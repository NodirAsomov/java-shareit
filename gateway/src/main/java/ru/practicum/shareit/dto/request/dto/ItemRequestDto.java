package ru.practicum.shareit.dto.request.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestDto {

    @NotBlank
    private String description;
}