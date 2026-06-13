package ru.practicum.shareit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RequestCreateDto {

    @NotBlank(message = "Description must not be blank")
    @Size(max = 2000, message = "Description is too long")
    private String description;

    public RequestCreateDto() {
    }

    public RequestCreateDto(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
