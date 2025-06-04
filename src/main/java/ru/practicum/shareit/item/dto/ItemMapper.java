package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id((long) item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
    }

    public static Item mapToNewItem(NewItemDto requestItemDto) {
        return Item.builder()
                .name(requestItemDto.getName())
                .description(requestItemDto.getDescription())
                .available(requestItemDto.getAvailable())
                .build();
    }

    public static Item updateItemFields(Item item, UpdateItemDto requestItemDto) {
        if (requestItemDto.hasName()) {
            item.setName(requestItemDto.getName());
        }
        if (requestItemDto.hasDescription()) {
            item.setDescription(requestItemDto.getDescription());

        }
        if (requestItemDto.hasAvailable()) {
            item.setAvailable(requestItemDto.getAvailable());
        }

        return item;
    }
}