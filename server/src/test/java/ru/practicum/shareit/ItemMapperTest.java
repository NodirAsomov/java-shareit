package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItem_shouldMapCorrectly() {
        ItemDto dto = ItemDto.builder()
                .name("item")
                .description("desc")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(dto, 1L);

        assertNotNull(item);
        assertEquals("item", item.getName());
        assertEquals("desc", item.getDescription());
        assertTrue(item.getAvailable());
        assertNotNull(item.getOwner());
        assertEquals(1L, item.getOwner().getId());
    }

    @Test
    void toDto_shouldMapCorrectly() {
        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(10L);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(false);
        item.setOwner(owner);

        ItemDto dto = ItemMapper.toDto(item);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("item", dto.getName());
        assertEquals("desc", dto.getDescription());
        assertFalse(dto.getAvailable());
        assertEquals(2L, dto.getOwnerId());
    }
}