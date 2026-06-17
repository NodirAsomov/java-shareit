package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestResponseDto create(Long userId, ItemRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setCreated(LocalDateTime.now());
        request.setRequestor(user);

        ItemRequest saved = requestRepository.save(request);

        return toDto(saved, List.of());
    }

    @Override
    public List<ItemRequestResponseDto> getOwn(Long userId) {

        List<ItemRequest> requests =
                requestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        return requests.stream()
                .map(r -> toDto(r, getItems(r.getId())))
                .toList();
    }

    @Override
    public List<ItemRequestResponseDto> getAll(Long userId) {

        List<ItemRequest> requests =
                requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);

        return requests.stream()
                .map(r -> toDto(r, getItems(r.getId())))
                .toList();
    }

    @Override
    public ItemRequestResponseDto getById(Long userId, Long requestId) {

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        return toDto(request, getItems(requestId));
    }


    private List<RequestItemDto> getItems(Long requestId) {

        return itemRepository.findByRequestId(requestId)
                .stream()
                .map(i -> RequestItemDto.builder()
                        .id(i.getId())
                        .name(i.getName())
                        .ownerId(i.getOwner().getId())
                        .build())
                .toList();
    }

    private ItemRequestResponseDto toDto(ItemRequest request,
                                         List<RequestItemDto> items) {

        return ItemRequestResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }
}