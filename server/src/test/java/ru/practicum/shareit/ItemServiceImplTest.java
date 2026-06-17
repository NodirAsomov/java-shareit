package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ru.practicum.shareit.item.CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Power tool");
        item.setAvailable(true);
        item.setOwner(user);
    }


    @Test
    void create_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ItemDto dto = new ItemDto();

        assertThrows(NotFoundException.class,
                () -> itemService.create(1L, dto));
    }

    @Test
    void create_shouldSaveItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto dto = new ItemDto();
        dto.setName("Drill");

        ItemDto result = itemService.create(1L, dto);

        assertEquals("Drill", result.getName());
    }


    @Test
    void get_shouldThrow_whenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.get(1L));
    }

    @Test
    void get_shouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(1L))
                .thenReturn(List.of());

        ItemDto result = itemService.get(1L);

        assertEquals(item.getName(), result.getName());
    }


    @Test
    void update_shouldThrow_whenNotOwner() {
        User another = new User();
        another.setId(2L);
        item.setOwner(another);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto dto = new ItemDto();

        assertThrows(NotFoundException.class,
                () -> itemService.update(1L, 1L, dto));
    }

    @Test
    void update_shouldUpdateName() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto dto = new ItemDto();
        dto.setName("NewName");

        ItemDto result = itemService.update(1L, 1L, dto);

        assertEquals("NewName", result.getName());
    }


    @Test
    void search_shouldReturnEmpty_whenTextBlank() {
        assertTrue(itemService.search(" ").isEmpty());
    }


    @Test
    void getOwnerItems_shouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(any(), any()))
                .thenReturn(List.of());
        when(commentRepository.findByItemIdInOrderByCreatedDesc(any()))
                .thenReturn(List.of());

        List<ItemDto> result = itemService.getOwnerItems(1L);

        assertEquals(1, result.size());
    }


    @Test
    void addComment_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, 1L, null));
    }
}