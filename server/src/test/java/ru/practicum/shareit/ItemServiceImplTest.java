package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
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
import static org.mockito.ArgumentMatchers.*;
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
    private final Long itemId = 10L;

    private Item buildItem() {
        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    // ---------------- USER CHECK ----------------

    @Test
    void create_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.create(userId, new ItemDto()));
    }

    // ---------------- CREATE ----------------

    @Test
    void create_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ItemDto dto = new ItemDto();
        dto.setName("Item");

        assertNotNull(service.create(userId, dto));
    }

    // ---------------- UPDATE ----------------

    @Test
    void update_notOwner() {
        Item item = buildItem();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> service.update(999L, itemId, new ItemDto()));
    }

    @Test
    void update_blankFieldsIgnored() {
        Item item = buildItem();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ItemDto dto = new ItemDto();
        dto.setName("   ");
        dto.setDescription("");

        service.update(userId, itemId, dto);

        assertEquals("Item", item.getName());
    }

    @Test
    void update_success() {
        Item item = buildItem();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ItemDto dto = new ItemDto();
        dto.setName("New");
        dto.setDescription("New desc");
        dto.setAvailable(false);

        ItemDto result = service.update(userId, itemId, dto);

        assertEquals("New", item.getName());
        assertEquals("New desc", item.getDescription());
        assertFalse(item.getAvailable());
        assertNotNull(result);
    }

    // ---------------- GET ----------------

    @Test
    void get_notFound() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.get(itemId));
    }

    @Test
    void get_success() {
        Item item = buildItem();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(itemId))
                .thenReturn(List.of());

        ItemDto dto = service.get(itemId);

        assertNotNull(dto);
    }

    // ---------------- SEARCH ----------------

    @Test
    void search_nullBlank() {
        assertTrue(service.search(null).isEmpty());
        assertTrue(service.search("   ").isEmpty());
    }

    @Test
    void search_success() {
        Item item = buildItem();

        when(itemRepository.search("text")).thenReturn(List.of(item));

        List<ItemDto> result = service.search("text");

        assertEquals(1, result.size());
    }

    // ---------------- OWNER ITEMS ----------------

    @Test
    void getOwnerItems_allBranches() {
        Item item = buildItem();

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));

        LocalDateTime now = LocalDateTime.now();

        Booking past = new Booking();
        past.setId(1L);
        past.setItem(item);
        past.setStart(now.minusDays(2));
        past.setBooker(new User());

        Booking future = new Booking();
        future.setId(2L);
        future.setItem(item);
        future.setStart(now.plusDays(2));
        future.setBooker(new User());

        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(
                anyList(), eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(past, future));

        when(commentRepository.findByItemIdInOrderByCreatedDesc(anyList()))
                .thenReturn(List.of());

        List<ItemDto> result = service.getOwnerItems(userId);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getLastBooking());
        assertNotNull(result.get(0).getNextBooking());
    }

    @Test
    void getOwnerItems_emptyBookings() {
        Item item = buildItem();

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));

        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(anyList(), any()))
                .thenReturn(List.of());

        when(commentRepository.findByItemIdInOrderByCreatedDesc(anyList()))
                .thenReturn(List.of());

        List<ItemDto> result = service.getOwnerItems(userId);

        assertEquals(1, result.size());
    }

    // ---------------- ADD COMMENT ----------------


        @Test
        void addComment_success() {
            Item item = buildItem();

            User author = new User();
            author.setId(userId);
            author.setName("User");

            when(userRepository.findById(userId)).thenReturn(Optional.of(author));
            when(userRepository.getReferenceById(userId)).thenReturn(author);

            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

            when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                    anyLong(), anyLong(), any(), any()
            )).thenReturn(true);

            when(commentRepository.save(any())).thenAnswer(i -> {
                Comment c = i.getArgument(0);
                c.setId(1L);
                c.setAuthor(author); // 🔥 ВАЖНО
                return c;
            });

            CommentDto dto = new CommentDto();
            dto.setText("ok");

            CommentDto result = service.addComment(userId, itemId, dto);

            assertNotNull(result);
        }

    

    @Test
    void addComment_noBooking() {
        Item item = buildItem();

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        assertThrows(ValidationException.class,
                () -> service.addComment(userId, itemId, new CommentDto()));
    }
}