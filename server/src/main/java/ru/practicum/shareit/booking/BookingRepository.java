package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //findAllByItemOwnerId
    //ALL
    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sortOrder);

    //STAT Wait, reject
    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sortOrder);

    //CURRENT
    List<Booking> findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqual(Long ownerId,
                                                                                  LocalDateTime now1,
                                                                                  LocalDateTime now2,
                                                                                  Sort sortOrder);

    //FUTURE
    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sortOrder);

    //PAST
    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sortOrder);

    //findAllByBookerId
    //ALL
    List<Booking> findAllByBookerId(Long bookerId, Sort sortOrder);

    //WAITING
    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sortOrder);

    //REJECTED, CANCELED
    List<Booking> findAllByBookerIdAndStatusIn(Long bookerId, List<BookingStatus> statuses, Sort sortOrder);

    //CURRENT
    List<Booking> findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(Long bookerId,
                                                                               LocalDateTime now1,
                                                                               LocalDateTime now2,
                                                                               Sort sortOrder);

    //FUTURE
    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sortOrder);

    //PAST
    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sortOrder);

    //other
    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemIdAndBookerIdAndEndBeforeAndStatus(Long itemId, Long userId, LocalDateTime now, BookingStatus bookingStatus);
}
