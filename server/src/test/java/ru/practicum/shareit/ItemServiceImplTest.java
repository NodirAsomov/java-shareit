package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRequestRepository requestRepository;

    @InjectMocks
    ItemServiceImpl service;

    User user;
    User otherUser;
    Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        otherUser = new User();
        otherUser.setId(2L);

        item = new Item();
        item.setId(10L);
        item.setOwner(user);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
    }

    // ---------------- CREATE ----------------

    @Test
    void create_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ItemDto dto = new ItemDto();
        dto.setName("item");

        assertDoesNotThrow(() -> service.create(1L, dto));
    }

    @Test
    void create_withRequest_success() {
        ItemRequest request = new ItemRequest();
        request.setId(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(99L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenAnswer(i -> {
            Item saved = i.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ItemDto dto = new ItemDto();
        dto.setName("item");
        dto.setDescription("desc");
        dto.setAvailable(true);
        dto.setRequestId(99L);

        ItemDto result = service.create(1L, dto);

        assertEquals(99L, result.getRequestId());
    }

    // ---------------- UPDATE ----------------

    @Test
    void update_success() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ItemDto dto = new ItemDto();
        dto.setName("new");

        ItemDto result = service.update(1L, 10L, dto);

        assertNotNull(result);
        verify(itemRepository).save(any());
    }

    @Test
    void update_forbidden() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(AccessException.class,
                () -> service.update(2L, 10L, new ItemDto()));
    }

    @Test
    void update_partial_fields() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ItemDto dto = new ItemDto();
        dto.setName("new");
        dto.setDescription(null);
        dto.setAvailable(true);

        service.update(1L, 10L, dto);

        verify(itemRepository).save(any());
    }

    // ---------------- GET ----------------

    @Test
    void get_success() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(10L))
                .thenReturn(List.of());

        ItemDto result = service.get(10L);

        assertNotNull(result);
    }

    @Test
    void get_not_found() {
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.get(10L));
    }

    // ---------------- SEARCH ----------------

    @Test
    void search_blank() {
        assertEquals(0, service.search(null).size());
        assertEquals(0, service.search("").size());
        assertEquals(0, service.search(" ").size());
    }

    @Test
    void search_success() {
        when(itemRepository.search("text")).thenReturn(List.of(item));

        List<ItemDto> result = service.search("text");

        assertEquals(1, result.size());
    }

    // ---------------- ADD COMMENT ----------------

    @Test
    void addComment_fail_no_booking() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), eq(BookingStatus.APPROVED), any()
        )).thenReturn(false);

        assertThrows(ValidationException.class,
                () -> service.addComment(1L, 10L, new CommentDto()));
    }

    @Test
    void addComment_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), eq(BookingStatus.APPROVED), any()
        )).thenReturn(true);

        when(commentRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() ->
                service.addComment(1L, 10L, new CommentDto()));
    }

    // ---------------- OWNER ITEMS (CRITICAL) ----------------

    @Test
    void getOwnerItems_empty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of());

        List<ItemDto> result = service.getOwnerItems(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getOwnerItems_with_bookings_and_comments() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));

        List<Long> itemIds = List.of(10L);

        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(
                eq(itemIds),
                eq(BookingStatus.APPROVED)
        )).thenReturn(List.of());

        Comment comment = new Comment();
        comment.setItem(item);

        User author = new User();
        author.setId(2L);
        author.setName("John");

        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        when(commentRepository.findByItemIdInOrderByCreatedDesc(eq(itemIds)))
                .thenReturn(List.of(comment));

        List<ItemDto> result = service.getOwnerItems(1L);

        assertFalse(result.isEmpty());
    }

    @Test
    void getOwnerItems_only_future_booking() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));

        Booking booking = mock(Booking.class);
        when(booking.getItem()).thenReturn(item);
        when(booking.getStart()).thenReturn(LocalDateTime.now().plusDays(1));


        when(bookingRepository.findByItemIdInAndStatusOrderByStartAsc(
                anyList(), eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(booking));

        when(commentRepository.findByItemIdInOrderByCreatedDesc(anyList()))
                .thenReturn(List.of());

        List<ItemDto> result = service.getOwnerItems(1L);

        assertFalse(result.isEmpty());
    }
}
