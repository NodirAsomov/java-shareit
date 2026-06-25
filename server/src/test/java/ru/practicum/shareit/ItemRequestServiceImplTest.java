package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Pageable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private User user;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need drill");
    }


    @Test
    void create_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.create(1L, new ItemRequestDto()));
    }

    @Test
    void create_shouldSaveRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Need drill");

        ItemRequestResponseDto result = service.create(1L, dto);

        assertEquals("Need drill", result.getDescription());
    }


    @Test
    void getOwn_shouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));

        when(itemRepository.findByRequestIdIn(List.of(1L)))
                .thenReturn(List.of());

        List<ItemRequestResponseDto> result = service.getOwn(1L);

        assertEquals(1, result.size());
    }


    @Test
    void getAll_shouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorIdNot(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(request));

        when(itemRepository.findByRequestIdIn(List.of(1L)))
                .thenReturn(List.of());

        List<ItemRequestResponseDto> result = service.getAll(1L, 0, 10);

        assertEquals(1, result.size());
    }


    @Test
    void getById_shouldThrow_whenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getById(1L, 1L));
    }

    @Test
    void getById_shouldReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(itemRepository.findByRequestIdIn(List.of(1L)))
                .thenReturn(List.of());

        ItemRequestResponseDto result = service.getById(1L, 1L);

        assertEquals(request.getDescription(), result.getDescription());
    }
}
