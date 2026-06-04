package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {

        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {

        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text) {

        return itemService.search(text);
    }
}