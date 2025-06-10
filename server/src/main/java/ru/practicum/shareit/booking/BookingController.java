package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BookingController {
    BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody NewBookingDto newBookingDto) {
        return bookingService.create(userId, newBookingDto);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsForBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam BookingState state) {
        return bookingService.findAllBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam BookingState state) {
        return bookingService.findAllBookingsByOwnerId(userId, state);
    }


    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("bookingId") Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatusBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("bookingId") Long bookingId,
                                          @RequestParam(name = "approved") Boolean approved) {
        return bookingService.updateStatusBooking(userId, bookingId, approved);
    }
}
