package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage requestStorage;
    private final UserStorage userStorage;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {

        User user = userStorage.getById(userId);

        ItemRequest request = ItemRequestMapper.fromDto(dto);
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toDto(requestStorage.add(request));
    }

    @Override
    public ItemRequestDto getById(Long requestId) {
        return ItemRequestMapper.toDto(requestStorage.getById(requestId));
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return requestStorage.getAll()
                .stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getByUser(Long userId) {
        return requestStorage.getAll()
                .stream()
                .filter(r -> r.getRequestor() != null)
                .filter(r -> r.getRequestor().getId().equals(userId))
                .map(ItemRequestMapper::toDto)
                .toList();
    }
}
