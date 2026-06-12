package ru.practicum.shareit.item.service;

import ru.practicum.dto.CommentDto;

import ru.practicum.dto.ItemDto;


import java.util.List;

public interface ItemService {

    List<ItemDto> getOwnerItems(Long userId);

    ItemDto get(Long id);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}