package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    @Transactional
    ItemDto create(Long userId, NewItemDto itemDto);

    @Transactional
    ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto);

    ItemDto getItem(Long owner, Long itemId);

    List<ItemDto> getItemsForOwner(Long owner);

    List<ItemDto> itemSearch(String text);

    @Transactional
    CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto);
}