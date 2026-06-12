package ru.practicum.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDto {

    @NotBlank
    @Size(max = 200)
    private String description;
}