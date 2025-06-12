package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceTest {
    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    static NewUserDto user1;
    static NewUserDto user2;
    static NewItemDto item1;

    @BeforeAll
    static void beforeAll() {
        user1 = NewUserDto.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = NewUserDto.builder().name("Yandex2").email("yandex2@practicum.ru").build();
        item1 = NewItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
    }

    @Test
    void create() {
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.create(user4.getId(), booking);

        assertThat(newBooking.getId()).isNotNull();
        assertThat(newBooking.getStart()).isEqualTo(booking.getStart());
        assertThat(newBooking.getEnd()).isEqualTo(booking.getEnd());
        assertThat(newBooking.getItem().getId()).isEqualTo(booking.getItemId());
        assertThat(newBooking.getBooker().getId()).isEqualTo(user4.getId());
        assertThat(newBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void throwExceptionWhenIdIsNull() {
        UserDto user3 = userService.create(user1);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        assertThatThrownBy(() -> bookingService.create(null, booking))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void throwExceptionWhenItemIsNotAvailable() {
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        NewItemDto item2 = NewItemDto.builder()
                .name("Yandex")
                .description("YandexPracticum")
                .available(false)
                .build();
        ItemDto item = itemService.create(user3.getId(), item2);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        assertThatThrownBy(() -> bookingService.create(user4.getId(), booking))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void throwExceptionWhenBookingStartEqualToEnd() {
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        assertThatThrownBy(() -> bookingService.create(user4.getId(), booking))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void throwExceptionWhenOwnerCreateBooking() {
        UserDto user3 = userService.create(user1);
        NewBookingDto booking = NewBookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        assertThatThrownBy(() -> bookingService.create(user3.getId(), booking))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void updateStatusBooking() {
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.create(user4.getId(), booking);
        BookingDto approvedBooking = bookingService.updateStatusBooking(user3.getId(), newBooking.getId(), true);

        assertThat(approvedBooking.getId()).isEqualTo(newBooking.getId());
        assertThat(approvedBooking.getStart()).isEqualTo(newBooking.getStart());
        assertThat(approvedBooking.getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(approvedBooking.getItem()).isEqualTo(newBooking.getItem());
        assertThat(approvedBooking.getBooker()).isEqualTo(user4);
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void throwExceptionWhenUserIsNotOwnerWhenUpdateBooking() {
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.create(user4.getId(), booking);

        assertThatThrownBy(() -> bookingService.updateStatusBooking(user4.getId(), newBooking.getId(), true))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void findById() {
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto bookingDto = bookingService.create(user4.getId(), booking);
        BookingDto getBooking = bookingService.findById(user4.getId(), bookingDto.getId());
        BookingDto getBookingOwner = bookingService.findById(user3.getId(), bookingDto.getId());

        assertThat(getBooking.getId()).isEqualTo(bookingDto.getId());
        assertThat(getBooking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(getBooking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(getBooking.getItem()).isEqualTo(bookingDto.getItem());
        assertThat(getBooking.getBooker()).isEqualTo(user4);
        assertThat(getBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

        assertThat(getBookingOwner.getId()).isEqualTo(bookingDto.getId());
        assertThat(getBookingOwner.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(getBookingOwner.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(getBookingOwner.getItem()).isEqualTo(bookingDto.getItem());
        assertThat(getBookingOwner.getBooker()).isEqualTo(user4);
        assertThat(getBookingOwner.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByUser() {
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.create(user4.getId(), booking);
        List<BookingDto> bookings = bookingService.findAllBookingsByBookerId(user4.getId(), BookingState.ALL).stream().toList();

        assertThat(bookings.getFirst().getId()).isEqualTo(newBooking.getId());
        assertThat(bookings.getFirst().getStart()).isEqualTo(newBooking.getStart());
        assertThat(bookings.getFirst().getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(bookings.getFirst().getItem()).isEqualTo(newBooking.getItem());
        assertThat(bookings.getFirst().getBooker()).isEqualTo(user4);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByOwner() {
        UserDto user3 = userService.create(user1);
        UserDto user4 = userService.create(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingDto booking = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.create(user4.getId(), booking);
        List<BookingDto> bookings = bookingService.findAllBookingsByOwnerId(user3.getId(), BookingState.ALL).stream().toList();

        assertThat(bookings.getFirst().getId()).isEqualTo(newBooking.getId());
        assertThat(bookings.getFirst().getStart()).isEqualTo(newBooking.getStart());
        assertThat(bookings.getFirst().getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(bookings.getFirst().getItem()).isEqualTo(newBooking.getItem());
        assertThat(bookings.getFirst().getBooker()).isEqualTo(user4);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}
