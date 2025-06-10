package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RequestController {
    RequestService requestService;

    @PostMapping
    public RequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody NewRequestDto newRequestDto) {
        return requestService.create(userId, newRequestDto);
    }

    @GetMapping
    public List<RequestDto> getAllRequestsById(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllRequestsById(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @RequestParam(name = "size", defaultValue = "50") Integer size) {
        return requestService.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("requestId") Long requestId) {
        return requestService.findById(userId, requestId);
    }
}
