package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class ItemDtoMapper {
    public static Item toItemDto(ItemDto item, int id, Object owner) {
        return new Item(
                id,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                (User) owner
        );
    }
}