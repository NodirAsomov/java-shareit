package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        ItemRequest request = ItemRequest.builder()
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .requestor(user)
                .build();

        request = requestRepository.save(request);

        return ItemRequestMapper.toDto(request, List.of());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {

        return requestRepository.findByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(req -> ItemRequestMapper.toDto(
                        req,
                        itemRepository.findByRequest_Id(req.getId())
                ))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllOtherRequests(Long userId) {

        return requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(req -> ItemRequestMapper.toDto(
                        req,
                        itemRepository.findByRequest_Id(req.getId())
                ))
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {

        ItemRequest req = requestRepository.findById(requestId)
                .orElseThrow();

        return ItemRequestMapper.toDto(
                req,
                itemRepository.findByRequest_Id(requestId)
        );
    }
}