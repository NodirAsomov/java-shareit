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

        User owner = new User();
        owner.setId(1L);
        owner.setName("user");
        owner.setEmail("user@mail.com");

        ItemDto dto = ItemDto.builder()
                .name("drill")
                .description("power drill")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(dto, owner);

        assertNotNull(item);
        assertEquals("drill", item.getName());
        assertEquals("power drill", item.getDescription());
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
        item.setName("hammer");
        item.setDescription("steel hammer");
        item.setAvailable(false);
        item.setOwner(owner);

        ItemDto dto = ItemMapper.toDto(item);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("hammer", dto.getName());
        assertEquals("steel hammer", dto.getDescription());
        assertFalse(dto.getAvailable());
        assertEquals(2L, dto.getOwnerId());
    }

    @Test
    void toDto_shouldHandleNullOwner() {

        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(null);

        ItemDto dto = ItemMapper.toDto(item);

        assertNotNull(dto);
        assertNull(dto.getOwnerId());
    }
}