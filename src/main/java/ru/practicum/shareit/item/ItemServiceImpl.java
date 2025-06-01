package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserService userService;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, NewItemDto itemDto) {
        User owner = userService.validateUserExist(userId);
        Item item = mapToNewItem(itemDto);
        item.setOwner(owner);
        return mapToItemDto(itemRepository.save(item));
    }


    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto) {
        userService.validateUserExist(userId);
        Item item = validateItemExist(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Предмет аренды не принадлежит данному пользователю");
        }
        updateItemFields(item, itemDto);
        item = itemRepository.save(item);
        return mapToItemDto(item);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = validateItemExist(itemId);
        userService.validateUserExist(userId);

        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        loadDetails(itemDto);

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsForOwner(Long ownerId) {
        userService.validateUserExist(ownerId);
        List<ItemDto> itemDtos = itemRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .peek(this::loadDetails)
                .collect(Collectors.toList());
        return itemDtos;
    }


    @Override
    public List<ItemDto> itemSearch(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findItemsByNameOrDescription(text).stream()
                .map(ItemMapper::mapToItemDto).toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto) {
        Item item = validateItemExist(itemId);
        User author = userService.validateUserExist(userId);
        validateCommentAuthorAndDate(userId, itemId);

        Comment comment = CommentMapper.mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private Item validateItemExist(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет аренды с id %d не найден.", itemId)));
    }

    private boolean validateItemAndCommentAuthorAndDate(Long userId, Long itemId) {
        validateItemExist(itemId);
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);

        if (bookings.isEmpty()) {
            throw new ValidationException("Для данной вещи ещё не было бронирований! Вы не можете оставить комментарий!");
        }

        Booking booking = bookings.stream()
                .filter(booking1 -> booking1.getBooker().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ForbiddenException("Пользователь не имеет права оставлять комментарий, " +
                        "так как не был арендатором!"));

        if (!(booking.getStatus() == BookingStatus.APPROVED)) {
            throw new ForbiddenException("Пользователь не имеет подтвержденного бронирования!");
        }

        if (!booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Пользователь не имеет завершенного бронирования!");
        }

        return true;
    }

    @Transactional
    private void loadDetails(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItemId((long) itemDto.getId());
        itemDto.setComments(comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .toList());

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc((long) itemDto.getId());

        if (!bookings.isEmpty()) {
            Booking nextBooking = bookings.get(0);
            itemDto.setNextBooking(BookingMapper.mapToBookingDto(nextBooking));

            if (bookings.size() > 1) {
                Booking lastBooking = bookings.get(1);
                itemDto.setLastBooking(BookingMapper.mapToBookingDto(lastBooking));
            } else {
                itemDto.setLastBooking(BookingMapper.mapToBookingDto(nextBooking));
            }
        }
    }

    private void validateCommentAuthorAndDate(Long userId, Long itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);

        if (bookings.isEmpty()) {
            throw new ValidationException("Для данной вещи ещё не было бронирований! Вы не можете оставить комментарий!");
        }

        Booking booking = bookings.stream()
                .filter(booking1 -> booking1.getBooker().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ForbiddenException("Пользователь не имеет права оставлять комментарий, " +
                        "так как не был арендатором!"));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new ForbiddenException("Пользователь не имеет подтвержденного бронирования!");
        }

        if (!booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Пользователь не имеет завершенного бронирования!");
        }
    }

}