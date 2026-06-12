package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    public ItemRequestDto create(Long userId, String description) {

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());

        request = requestRepository.save(request);

        return mapToDto(request, List.of());
    }


    public List<ItemRequestDto> getOwn(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ItemRequest> requests =
                requestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        return requests.stream()
                .map(r -> {
                    List<Item> items = itemRepository.findByRequestId(r.getId());
                    return mapToDto(r, items);
                })
                .toList();
    }


    public List<ItemRequestDto> getAllExceptUser(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ItemRequest> requests =
                requestRepository.findByRequesterIdNotOrderByCreatedDesc(userId);

        return requests.stream()
                .map(r -> {
                    List<Item> items = itemRepository.findByRequestId(r.getId());
                    return mapToDto(r, items);
                })
                .toList();
    }


    public ItemRequestDto getById(Long userId, Long requestId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        List<Item> items = itemRepository.findByRequestId(requestId);

        return mapToDto(request, items);
    }


    private ItemRequestDto mapToDto(ItemRequest request, List<Item> items) {

        List<ItemRequestDto.ItemResponse> itemDtos = items.stream()
                .map(i -> new ItemRequestDto.ItemResponse(
                        i.getId(),
                        i.getName(),
                        i.getOwner().getId()
                ))
                .toList();

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemDtos
        );
    }
}