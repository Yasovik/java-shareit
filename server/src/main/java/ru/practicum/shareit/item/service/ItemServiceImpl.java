package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserService userService;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    RequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDto create(Long userId, NewItemDto itemDto) {
        User owner = userService.validateUserExist(userId);
        Item item = mapToNewItem(itemDto);
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден."));
            item.setRequest(request);
        }

        return mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        Item item = validateItemExist(itemId);
        userService.validateUserExist(userId);

        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        loadDetails(itemDto);

        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto) {
        userService.validateUserExist(userId);
        Item item = validateItemExist(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Предмет аренды не принадлежит данному пользователю");
        }
        if (itemDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден."));
            item.setRequest(request);
        }

        updateItemFields(item, itemDto);
        item = itemRepository.save(item);
        return mapToItemDto(item);
    }

    @Override
    public List<ItemDto> findItemsByOwnerId(Long ownerId) {
        userService.validateUserExist(ownerId);

        List<ItemDto> itemDtos = itemRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(ItemMapper::mapToItemDto).toList();

        itemDtos.forEach(this::loadDetails);

        return itemDtos;
    }

    @Override
    public List<ItemDto> findItemsByNameOrDescription(Long userId, String text) {
        userService.validateUserExist(userId);
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findItemsByNameOrDescription(text).stream()
                .map(ItemMapper::mapToItemDto).toList();
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentDto newCommentDto) {
        Item item = validateItemExist(itemId);
        User author = userService.validateUserExist(userId);
        validateCommentAuthorAndDate(userId, itemId);

        Comment comment = CommentMapper.mapToComment(newCommentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private Item validateItemExist(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет аренды с id %d не найден.", itemId)));
    }

    private void validateCommentAuthorAndDate(Long userId, Long itemId) {
        List<Booking> bookingList = bookingRepository
                .findAllByItemIdAndBookerIdAndEndBeforeAndStatus(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (bookingList.isEmpty()) {
            throw new ValidationException("""
                    Пользователь не  имеет права оставлять комментарий,
                    так как не был арендатором или не имеет завершенного бронирования!""");
        }
    }

    @Transactional
    private void loadDetails(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        itemDto.setComments(comments.stream().map(CommentMapper::mapToCommentDto).toList());

        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(itemDto.getId(),
                Sort.by(Sort.Direction.DESC, "start"));

        if (!bookings.isEmpty()) {
            Booking nextBooking = bookings.get(0);
            itemDto.setNextBooking(BookingMapper.mapToBookingDto(nextBooking));

            if (bookings.size() > 1) {
                itemDto.setLastBooking(BookingMapper.mapToBookingDto(bookings.get(1)));
            } else {
                itemDto.setLastBooking(BookingMapper.mapToBookingDto(nextBooking));
            }
        }
    }
}
