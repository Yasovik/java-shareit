package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {
    public static Item toItemDto(ItemDto item, int id, int owner) {
        return new Item(
                id,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                owner,
                item.getRequestId()
        );
    }
}