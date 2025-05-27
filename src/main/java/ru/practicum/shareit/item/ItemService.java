package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(ItemDto itemDto, Integer owner);

    Item update(ItemDto itemDto, Integer owner, int id);

    Item getItem(Integer owner);

    List<ItemDto> getItemsForOwner(Integer owner);

    List<ItemDto> itemSearch(String text);
}