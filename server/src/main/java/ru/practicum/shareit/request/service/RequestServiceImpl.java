package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    UserService userService;
    ItemRepository itemRepository;

    @Transactional
    @Override
    public RequestDto create(Long userId, NewRequestDto newRequestDto) {
        User requester = userService.validateUserExist(userId);
        Request request = RequestMapper.mapToRequest(newRequestDto);
        request.setRequester(requester);
        return RequestMapper.mapToRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getAllRequestsById(Long userId) {
        userService.validateUserExist(userId);
        return requestRepository.findAllByRequesterId(userId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(request -> {
                    RequestDto requestDto = RequestMapper.mapToRequestDto(request);
                    loadDetails(requestDto);
                    return requestDto;
                })
                .toList();
    }

    @Override
    public List<RequestDto> findAll(Long userId, Integer from, Integer size) {
        userService.validateUserExist(userId);
        if (from < 0 || size < 0) {
            throw new ValidationException("Аргументы не могут быть отрицательными.");
        }
        return requestRepository.findAll(PageRequest.of((from / size), size,
                        Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(request -> {
                    RequestDto requestDto = RequestMapper.mapToRequestDto(request);
                    loadDetails(requestDto);
                    return requestDto;
                })
                .toList();
    }

    @Override
    public RequestDto findById(Long userId, Long requestId) {
        userService.validateUserExist(userId);
        RequestDto requestDto = RequestMapper.mapToRequestDto(requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с ID = %d не найден!", requestId))));
        loadDetails(requestDto);
        return requestDto;
    }

    @Transactional
    private void loadDetails(RequestDto requestDto) {

        List<ItemDto> itemDtos = itemRepository.findByRequestIdOrderByRequestIdDesc(requestDto.getId()).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
        requestDto.setItems(itemDtos);
    }
}
