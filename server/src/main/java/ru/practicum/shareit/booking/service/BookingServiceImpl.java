package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserService userService;
    ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto create(Long userId, NewBookingDto newBookingDto) {
        validateDate(newBookingDto);
        User booker = userService.validateUserExist(userId);
        Item item = validateItemExist(newBookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Данная вещь не доступна для брони!");
        }
        Booking booking = BookingMapper.mapToNewBooking(newBookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        return BookingMapper.mapToBookingDto(validateBooking(userId, bookingId));
    }

    @Transactional
    @Override
    public BookingDto updateStatusBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = validateBookingExist(bookingId);
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Данная вещь не принадлежит этому пользователю");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> findAllBookingsByBookerId(Long bookerId, BookingState state) {
        userService.validateUserExist(bookerId);

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case BookingState.WAITING: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndStatus(bookerId,
                        BookingStatus.WAITING, sortOrder));
                break;
            }
            case BookingState.REJECTED: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndStatusIn(bookerId,
                        List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), sortOrder));
                break;
            }
            case BookingState.CURRENT: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(bookerId,
                        now, now, sortOrder));
                break;
            }
            case BookingState.FUTURE: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndStartAfter(bookerId,
                        now, sortOrder));
                break;
            }
            case BookingState.PAST: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndEndBefore(bookerId,
                        now, sortOrder));
                break;
            }
            case BookingState.ALL: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerId(bookerId, sortOrder));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    @Override
    public List<BookingDto> findAllBookingsByOwnerId(Long ownerId, BookingState state) {
        userService.validateUserExist(ownerId);

        List<Item> userItemsIds = itemRepository.findByOwnerIdOrderByIdAsc(ownerId);

        if (userItemsIds.isEmpty()) {
            throw new ValidationException("Этот запрос только для тех пользователей, которые имеют хотя бы 1 вещь");
        }

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case BookingState.WAITING: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId,
                        BookingStatus.WAITING, sortOrder));
                break;
            }
            case BookingState.REJECTED: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId,
                        BookingStatus.REJECTED, sortOrder));
                break;
            }
            case BookingState.CURRENT: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqual(ownerId,
                        now, now, sortOrder));
                break;
            }
            case BookingState.FUTURE: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId,
                        now, sortOrder));
                break;
            }
            case BookingState.PAST: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId,
                        now, sortOrder));
                break;
            }
            case BookingState.ALL: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerId(ownerId, sortOrder));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    private Booking validateBookingExist(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронь с id %d не найдена.", bookingId)));

    }

    private Booking validateBooking(Long userId, Long bookingId) {
        User user = userService.validateUserExist(userId);
        Booking booking = validateBookingExist(bookingId);
        Item item = booking.getItem();

        if (!booking.getBooker().equals(user) && !item.getOwner().equals(user)) {
            throw new ForbiddenException("Данная бронь не имеет отношения к пользователю");
        }

        return booking;
    }

    private void validateDate(NewBookingDto bookingDto) {
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidationException("Неверные даты старта и окончания брони");
        }
    }

    private Item validateItemExist(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет аренды с id %d не найден.", itemId)));
    }
}
