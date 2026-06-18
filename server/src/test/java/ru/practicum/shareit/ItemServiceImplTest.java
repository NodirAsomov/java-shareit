package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock private ItemRepository itemRepository;
    @Mock private UserRepository userRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl service;

    private final Long userId = 1L;
    private final Long itemId = 1L;

    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setName("user");

        item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
    }

    // ---------------- CREATE ----------------

    @Test
    void create_ok() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = service.create(userId, new ItemDto());

        assertNotNull(result);
        verify(itemRepository).save(any());
    }

    @Test
    void create_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.create(userId, new ItemDto()));
    }

    // ---------------- GET ----------------

    @Test
    void get_ok() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(itemId))
                .thenReturn(List.of());

        ItemDto result = service.get(itemId);

        assertNotNull(result);
    }

    @Test
    void get_notFound() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.get(itemId));
    }

    // ---------------- UPDATE ----------------

    @Test
    void update_notOwner() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> service.update(999L, itemId, new ItemDto()));
    }

    @Test
    void update_partialFields() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto dto = new ItemDto();
        dto.setName("new name");

        ItemDto result = service.update(userId, itemId, dto);

        assertNotNull(result);
        verify(itemRepository).save(any());
    }

    // ---------------- SEARCH ----------------

    @Test
    void search_emptyText() {
        assertTrue(service.search("").isEmpty());
    }

    @Test
    void search_blankText() {
        assertTrue(service.search("   ").isEmpty());
    }

    @Test
    void search_ok_filterUnavailable() {

        User owner = new User();
        owner.setId(userId);
        owner.setName("owner");

        Item i1 = new Item();
        i1.setOwner(owner);
        i1.setAvailable(true);

        Item i2 = new Item();
        i2.setOwner(owner);
        i2.setAvailable(false);

        when(itemRepository.search("text")).thenReturn(List.of(i1, i2));

        List<ItemDto> result = service.search("text");

        assertEquals(1, result.size());
    }

    // ---------------- ADD COMMENT ----------------

    @Test
    void addComment_noBooking() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                any(), any(), any(), any()
        )).thenReturn(false);

        assertThrows(ValidationException.class,
                () -> service.addComment(userId, itemId, new CommentDto()));
    }

    @Test
    void addComment_ok() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                any(), any(), any(), any()
        )).thenReturn(true);

        when(commentRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        CommentDto result = service.addComment(userId, itemId, new CommentDto());

        assertNotNull(result);
    }

    // ---------------- OWNER ITEMS ----------------

    @Test
    void getOwnerItems_noBookings() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));
        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(any(), any()))
                .thenReturn(List.of());
        when(commentRepository.findByItemIdInOrderByCreatedDesc(any()))
                .thenReturn(List.of());

        List<ItemDto> result = service.getOwnerItems(userId);

        assertEquals(1, result.size());
    }

    @Test
    void getOwnerItems_withPastAndFutureBookings() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));

        Booking past = new Booking();
        past.setItem(item);
        past.setStart(LocalDateTime.now().minusDays(2));

        Booking future = new Booking();
        future.setItem(item);
        future.setStart(LocalDateTime.now().plusDays(2));

        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(any(), any()))
                .thenReturn(List.of(past, future));

        when(commentRepository.findByItemIdInOrderByCreatedDesc(any()))
                .thenReturn(List.of());

        List<ItemDto> result = service.getOwnerItems(userId);

        assertEquals(1, result.size());
    }
}