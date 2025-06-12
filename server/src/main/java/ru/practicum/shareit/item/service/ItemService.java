package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, NewItemDto itemDto);

    ItemDto findById(Long userId, Long itemId);

    ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto);

    List<ItemDto> findItemsByOwnerId(Long userId);

    List<ItemDto> findItemsByNameOrDescription(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto);
}
