package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.marker.Marker;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    ItemService service;

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long owner,
                          @Valid @RequestBody NewItemDto itemDto) {
        log.info("Запрос на создание вещи");
        return service.create(owner, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long owner,
                          @Valid @RequestBody UpdateItemDto itemDto, @PathVariable("id") long id) {
        log.info("Запрос на обновление вещи");
        return service.update(owner,id, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") int itemId) {
        log.info("Запрос на получение вещи с id {}", itemId);
        return service.getItem(userId, Long.valueOf(itemId));
    }

    @GetMapping
    public List<ItemDto> getItemsForOwner(@RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Запрос на получение вещей пользователя id {}", owner);
        return service.getItemsForOwner(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Поиск вещей по запросу: {}", text);
        return service.itemSearch(text);
    }

    @PostMapping("{id}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long owner,
                                 @PathVariable("id") Long
                                         id,
                                 @Valid @RequestBody NewCommentDto commentDto
    ) {
        return service.addComment(owner, id, commentDto);
    }
}