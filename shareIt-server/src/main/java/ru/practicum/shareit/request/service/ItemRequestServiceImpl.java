package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        ItemRequest saved = repository.save(request);

        return ItemRequestMapper.toDto(saved);
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {

        List<ItemRequest> requests =
                repository.findByRequestor_IdOrderByCreatedDesc(userId);

        List<Long> ids = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<Item> items = itemRepository.findByRequest_IdIn(ids);

        Map<Long, List<Item>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));

        return requests.stream()
                .map(req -> {
                    ItemRequestDto dto = ItemRequestMapper.toDto(req);

                    List<ItemShortDto> reqItems =
                            itemsByRequest.getOrDefault(req.getId(), List.of())
                                    .stream()
                                    .map(i -> new ItemShortDto(i.getId(), i.getName(), i.getOwner().getId()))
                                    .toList();

                    dto.setItems(reqItems);

                    return dto;
                })
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {

        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        return ItemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {

        Pageable page = PageRequest.of(from / size, size);

        List<ItemRequest> requests =
                repository.findByRequestor_IdNot(userId, page);

        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }
}