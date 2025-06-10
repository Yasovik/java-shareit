package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceTest {
    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    @Autowired
    RequestService requestService;

    static NewUserDto user1;
    static NewUserDto user2;
    static NewItemDto item1;
    static NewItemDto item2;

    @BeforeAll
    static void beforeAll() {
        user1 = NewUserDto.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = NewUserDto.builder().name("Yandex2").email("yandex2@practicum.ru").build();
        item1 = NewItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
        item2 = NewItemDto.builder().name("Yandex2").description("YandexPracticum2").available(true).build();
    }

    @Test
    void createAndGetItem() {
        UserDto user3 = userService.create(user1);
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto getItem = itemService.findById(user3.getId(), item.getId());

        assertThat(item.getId()).isEqualTo(getItem.getId());
        assertThat(item.getName()).isEqualTo(getItem.getName());
        assertThat(item.getDescription()).isEqualTo(getItem.getDescription());
        assertThat(item.getAvailable()).isEqualTo(getItem.getAvailable());
        assertThat(item.getRequestId()).isNull();
    }

    @Test
    void createItemWithRequest() {
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        NewRequestDto itemRequestDto = NewRequestDto.builder().description("Yandex").build();
        RequestDto itemRequest = requestService.create(user4.getId(), itemRequestDto);
        NewItemDto item3 = NewItemDto.builder()
                .name("Yandex2")
                .description("YandexPracticum2")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
        ItemDto item = itemService.create(user3.getId(), item3);
        ItemDto getItem = itemService.findById(user3.getId(), item.getId());
        assertThat(item.getId()).isEqualTo(getItem.getId());
        assertThat(item.getName()).isEqualTo(getItem.getName());
        assertThat(item.getDescription()).isEqualTo(getItem.getDescription());
        assertThat(item.getAvailable()).isEqualTo(getItem.getAvailable());
        assertThat(item.getRequestId()).isEqualTo(getItem.getRequestId());
    }

    @Test
    void throwExceptionWhenUserIsNotFound() {
        assertThatThrownBy(() -> itemService.create(1000L, item1)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void throwExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> itemService.create(null, item1)).isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void updateItem() {
        UpdateItemDto itemUpd = UpdateItemDto.builder().name("Yandex2").description("YandexPracticum2").available(true).build();
        UserDto user3 = userService.create(user1);
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto updateItem = itemService.update(user3.getId(), item.getId(), itemUpd);

        assertThat(updateItem.getId()).isEqualTo(item.getId());
        assertThat(updateItem.getName()).isEqualTo(item2.getName());
        assertThat(updateItem.getDescription()).isEqualTo(item2.getDescription());
        assertThat(updateItem.getAvailable()).isEqualTo(item2.getAvailable());
    }

    @Test
    void updateItemNameIsNull() {
        UserDto user3 = userService.create(user1);
        UpdateItemDto item3 = UpdateItemDto.builder().description("YandexPracticum2").available(false).build();
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto updateItem = itemService.update(user3.getId(), item.getId(), item3);

        assertThat(updateItem.getId()).isEqualTo(item.getId());
        assertThat(updateItem.getName()).isEqualTo(item1.getName());
        assertThat(updateItem.getDescription()).isEqualTo(item3.getDescription());
        assertThat(updateItem.getAvailable()).isEqualTo(item3.getAvailable());
    }

    @Test
    void updateItemDescriptionIsNull() {
        UserDto user3 = userService.create(user1);
        UpdateItemDto item3 = UpdateItemDto.builder().name("Yandex2").available(false).build();
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto updateItem = itemService.update(user3.getId(), item.getId(), item3);

        assertThat(updateItem.getId()).isEqualTo(item.getId());
        assertThat(updateItem.getName()).isEqualTo(item3.getName());
        assertThat(updateItem.getDescription()).isEqualTo(item1.getDescription());
        assertThat(updateItem.getAvailable()).isEqualTo(item3.getAvailable());
    }

    @Test
    void updateItemAvaliableIsNull() {
        UserDto user3 = userService.create(user1);
        UpdateItemDto item3 = UpdateItemDto.builder().name("Yandex2").description("YandexPracticum2").build();
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto updateItem = itemService.update(user3.getId(), item.getId(), item3);

        assertThat(updateItem.getId()).isEqualTo(item.getId());
        assertThat(updateItem.getName()).isEqualTo(item3.getName());
        assertThat(updateItem.getDescription()).isEqualTo(item3.getDescription());
        assertThat(updateItem.getAvailable()).isEqualTo(item1.getAvailable());
    }

    @Test
    void throwExceptionWhenUserIsNotOwnerWhenUpdateItem() {
        UpdateItemDto itemUpd = UpdateItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        ItemDto item = itemService.create(user3.getId(), item1);

        assertThatThrownBy(() -> itemService.update(user4.getId(), item.getId(), itemUpd))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getItemsByOwner() {
        UserDto user3 = userService.create(user1);
        itemService.create(user3.getId(), item1);
        ItemDto item = itemService.create(user3.getId(), item2);

        List<ItemDto> items = itemService.findItemsByOwnerId(user3.getId()).stream().toList();

        assertThat(items).hasSize(2);
        assertThat(items.get(1).getId()).isEqualTo(item.getId());
        assertThat(items.get(1).getName()).isEqualTo(item.getName());
        assertThat(items.get(1).getDescription()).isEqualTo(item.getDescription());
        assertThat(items.get(1).getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void addCommentToItem() {
        UserDto user4 = userService.create(user2);
        ItemDto item = itemService.create(user4.getId(), item1);

        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2025, 3, 10, 12, 0))
                .end(LocalDateTime.of(2025, 3, 10, 12, 0).plusNanos(1))
                .build();
        BookingDto newBooking = bookingService.create(user4.getId(), booking);
        bookingService.updateStatusBooking(user4.getId(), newBooking.getId(), true);

        NewCommentDto comment = NewCommentDto.builder().text("Text").build();
        CommentDto newComment = itemService.addComment(user4.getId(), item.getId(), comment);

        assertThat(newComment.getText()).isEqualTo(comment.getText());
        assertThat(newComment.getAuthorName()).isEqualTo(user4.getName());
    }

    @Test
    void throwExceptionWhenOwnerTryToComment() {
        UserDto user3 = userService.create(user1);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusNanos(1))
                .build();
        UserDto user4 = userService.create(user2);
        bookingService.create(user4.getId(), booking);
        NewCommentDto comment = NewCommentDto.builder().text("Text").build();

        assertThatThrownBy(() -> itemService.addComment(user3.getId(), item.getId(), comment))
                .isInstanceOf(ValidationException.class);
    }
}
