package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {
    @Transactional
    BookingDto create(Long userId, NewBookingDto newBookingDto);

    BookingDto findById(Long userId, Long bookingId);

    @Transactional
    BookingDto updateStatusBooking(Long userId, Long bookingId, boolean approved);

    List<BookingDto> findAllBookingsByBookerId(Long bookerId, BookingState state);

    List<BookingDto> findAllBookingsByOwnerId(Long ownerId, BookingState state);
}