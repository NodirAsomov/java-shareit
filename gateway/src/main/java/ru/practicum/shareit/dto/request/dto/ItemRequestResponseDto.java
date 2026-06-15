package ru.practicum.shareit.dto.request.dto;


import lombok.Data;
import ru.practicum.shareit.dto.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestResponseDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}