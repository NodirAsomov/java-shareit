package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toEntity_shouldMapCorrectly() {
        User requestor = new User();
        requestor.setId(1L);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need a drill");

        ItemRequest request = ItemRequestMapper.toEntity(dto, requestor);

        assertNotNull(request);
        assertEquals("Need a drill", request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertNotNull(request.getCreated());
    }

    @Test
    void toDto_shouldMapCorrectly() {
        LocalDateTime created = LocalDateTime.of(2026, 6, 25, 12, 0);
        ItemRequest request = ItemRequest.builder()
                .id(2L)
                .description("Need a saw")
                .created(created)
                .build();
        List<RequestItemDto> items = List.of(RequestItemDto.builder()
                .id(10L)
                .name("Saw")
                .ownerId(3L)
                .build());

        ItemRequestResponseDto dto = ItemRequestMapper.toDto(request, items);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("Need a saw", dto.getDescription());
        assertEquals(created, dto.getCreated());
        assertEquals(items, dto.getItems());
    }

    @Test
    void toRequestItemDto_shouldMapCorrectly() {
        User owner = new User();
        owner.setId(4L);

        Item item = new Item();
        item.setId(20L);
        item.setName("Hammer");
        item.setOwner(owner);

        RequestItemDto dto = ItemRequestMapper.toRequestItemDto(item);

        assertNotNull(dto);
        assertEquals(20L, dto.getId());
        assertEquals("Hammer", dto.getName());
        assertEquals(4L, dto.getOwnerId());
    }
}
