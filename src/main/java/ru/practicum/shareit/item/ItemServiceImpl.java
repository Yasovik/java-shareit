package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public Item create(ItemDto itemDto, Integer owner) {
        userRepository.getUser(owner);
        return itemRepository.create(itemDto, owner);
    }

    @Override
    public Item update(ItemDto itemDto, Integer owner, int id) {
        userRepository.getUser(owner);
        itemRepository.getItem(id);
        return itemRepository.update(itemDto, owner, id);
    }

    @Override
    public Item getItem(Integer id) {
        return itemRepository.getItem(id);
    }

    @Override
    public List<Item> getItemsForOwner(Integer owner) {
        return itemRepository.getItemsForOwner(owner);
    }

    @Override
    public List<Item> itemSearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        text = text.toLowerCase();
        return itemRepository.searchItems(text);
    }

}