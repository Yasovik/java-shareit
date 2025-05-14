package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.other.Marker;

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
    public Item create(@RequestHeader("X-Sharer-User-Id") Integer owner,
                       @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание вещи");
        return service.create(itemDto, owner);
    }

    @PatchMapping("/{id}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Integer owner,
                       @Valid @RequestBody ItemDto itemDto, @PathVariable("id") int id) {
        log.info("Запрос на обновление вещи");
        return service.update(itemDto, owner, id);
    }

    @GetMapping("/{id}")
    public Item getItem(@PathVariable("id") int id) {
        log.info("Запрос на получение вещи с id {}", id);
        return service.getItem(id);
    }

    @GetMapping
    public List<Item> getItemsForOwner(@RequestHeader("X-Sharer-User-Id") int owner) {
        log.info("Запрос на получение вещей пользователя id {}", owner);
        return service.getItemsForOwner(owner);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text) {
        log.info("Поиск вещей по запросу: {}", text);
        return service.itemSearch(text);
    }
}