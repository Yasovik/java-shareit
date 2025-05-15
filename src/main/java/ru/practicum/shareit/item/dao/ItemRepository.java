package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepository {
    private final Map<Integer, Map<Integer, Item>> items = new HashMap<>();
    private int id = 1;

    public Item create(ItemDto itemDto, int owner) {
        Item item = ItemDtoMapper.toItemDto(itemDto, id, owner);
        Map<Integer, Item> itemsMap;
        if (items.containsKey(owner)) {
            itemsMap = items.get(owner);
        } else {
            itemsMap = new HashMap<>();
        }
        itemsMap.put(id, item);
        items.put(owner, itemsMap);
        id++;
        log.info("Вещь успешно создана: {}", item);
        return item;
    }

    public Item update(ItemDto itemDto, int owner, int id) {
        Map<Integer, Item> itemsMap = items.get(owner);
        Item oldItem = itemsMap.get(id);

        if (itemDto.getName() != null && !itemDto.getName().isEmpty() && !itemDto.getName().isBlank()) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty() && !itemDto.getDescription().isBlank()) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            oldItem.setRequest(itemDto.getRequestId());
        }
        itemsMap.put(id, oldItem);
        items.put(owner, itemsMap);
        log.info("Вещь успешно обновлена: {}", itemDto);
        return oldItem;
    }

    public Item getItem(int id) {
        return items.values().stream()
                .map(map -> map.get(id))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Вещь с ID " + id + " не найдена"));

    }

    public List<ItemDto> getItemsForOwner(int owner) {
        try {
            return items.get(owner).values().stream().collect(Collectors.toList());
        } catch (NullPointerException e) {
            throw new NotFoundException("Вещи для пользователя с ID " + id + " не найдены");
        }
    }

    public List<ItemDto> searchItems(String text) {
        try {
            return items.values().stream()
                    .flatMap(userItems -> userItems.values().stream())
                    .filter(Objects::nonNull)
                    .filter(item -> (item.getName().toLowerCase().contains(text) ||
                            item.getDescription().toLowerCase().contains(text)) &&
                            item.getAvailable())
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
}