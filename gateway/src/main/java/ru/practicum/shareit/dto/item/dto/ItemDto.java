package ru.practicum.shareit.dto.item.dto;


import lombok.Data;

@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private Long requestId;
}