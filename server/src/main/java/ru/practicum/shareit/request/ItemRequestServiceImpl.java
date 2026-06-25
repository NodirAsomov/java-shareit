package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestResponseDto create(Long userId, ItemRequestDto dto) {

        User user = getUser(userId);
        ItemRequest request = ItemRequestMapper.toEntity(dto, user);
        ItemRequest saved = requestRepository.save(request);

        return ItemRequestMapper.toDto(saved, List.of());
    }

    @Override
    public List<ItemRequestResponseDto> getOwn(Long userId) {
        getUser(userId);

        List<ItemRequest> requests =
                requestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        Map<Long, List<RequestItemDto>> itemsByRequestId = getItemsByRequestId(requests);

        return requests.stream()
                .map(r -> ItemRequestMapper.toDto(
                        r,
                        itemsByRequestId.getOrDefault(r.getId(), List.of())
                ))
                .toList();
    }

    @Override
    public List<ItemRequestResponseDto> getAll(Long userId, int from, int size) {
        getUser(userId);

        if (from < 0 || size <= 0) {
            throw new ValidationException("Invalid pagination parameters");
        }

        List<ItemRequest> requests =
                requestRepository.findByRequestorIdNot(
                        userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"))
                );

        Map<Long, List<RequestItemDto>> itemsByRequestId = getItemsByRequestId(requests);

        return requests.stream()
                .map(r -> ItemRequestMapper.toDto(
                        r,
                        itemsByRequestId.getOrDefault(r.getId(), List.of())
                ))
                .toList();
    }

    @Override
    public ItemRequestResponseDto getById(Long userId, Long requestId) {
        getUser(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found with id=" + requestId));

        return ItemRequestMapper.toDto(
                request,
                getItemsByRequestId(List.of(request)).getOrDefault(requestId, List.of())
        );
    }

    private Map<Long, List<RequestItemDto>> getItemsByRequestId(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        if (requestIds.isEmpty()) {
            return Map.of();
        }

        return itemRepository.findByRequestIdIn(requestIds).stream()
                .filter(item -> item.getRequest() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemRequestMapper::toRequestItemDto, Collectors.toList())
                ));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));
    }
}
