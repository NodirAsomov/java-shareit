package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");
    }

    @Test
    void create_shouldThrowConflict_whenEmailExists() {
        UserDto dto = new UserDto();
        dto.setEmail("john@mail.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(dto));
    }

    @Test
    void create_shouldSaveUser() {
        UserDto dto = new UserDto();
        dto.setName("John");
        dto.setEmail("john@mail.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(dto);

        assertEquals("John", result.getName());
        assertEquals("john@mail.com", result.getEmail());
    }

    @Test
    void update_shouldThrowNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.update(1L, new UserDto()));
    }

    @Test
    void update_shouldUpdateName() {
        UserDto dto = new UserDto();
        dto.setName("NewName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1L, dto);

        assertEquals("NewName", result.getName());
    }

    @Test
    void get_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.get(1L);

        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void delete_shouldCallRepository() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}
