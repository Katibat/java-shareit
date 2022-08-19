package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    @Transactional
    public Booking save(Booking booking, Long userId, Long itemId) {
        Item item = itemService.findById(itemId);
        User user = userService.findById(userId);
        if (user.getId().equals(item.getOwner().getId()) && userId.equals(item.getOwner().getId())) {
            log.debug("Создание бронирования вещи {} запрощено собственником {}.", itemId, userId);
            throw new NotFoundException("Бронирование запрошено собственником вещи.");
        }
        if (!item.getAvailable()) {
            log.debug("При создании бронирования запрощена недоступная для бронирования вещь {}.", itemId);
            throw new ValidationException("Вещь недоступна для бронирования.");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            log.debug("При создании бронирования запрощена вещь {}, недоступная в указанный период.", itemId);
            throw new ValidationException("В указанный период вещь недоступна для бронирования.");
        }
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        log.info("Создано бронирование вещи {} пользователем {}.", itemId, user.getId());
        return repository.save(booking);
    }

    @Override
    @Transactional
    public void deleteById(Long bookingId) {
        log.info("Удалено бронирование вещи {}.", bookingId);
        repository.deleteById(bookingId);
    }

    @Override
    @Transactional
    public Booking changeBookingStatus(Long userId, Long bookingId, Boolean approved) {
        User user = userService.findById(userId);
        Booking booking = findById(userId, bookingId);
        Item item = itemService.findById(booking.getItem().getId());
        if (!user.getId().equals(item.getOwner().getId())) {
            log.debug("Попытка изменения статуса бронирования вещи {} не ее собственником {}.", item.getId(), userId);
            throw new NotFoundException("Недостаточно прав для изменения статуса бронирования вещи " +
                    "с идентификатором № " + item.getId());
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.debug("Попытка изменения статуса бронирования вещи {} для уже одобренного бронирования {}.",
                    item.getId(), bookingId);
            throw new StateException("Бронирование вещи уже было одобрено ранее.");
        }
        if (booking.getStatus().equals(BookingStatus.REJECTED)) {
            log.debug("Попытка изменения статуса бронирования вещи {} для уже отклоненного бронирования {}.",
                    item.getId(), bookingId);
            throw new StateException("Бронирование вещи уже было отклонено ранее.");
        }
        if (approved) {
            log.info("Cтатус бронирования вещи {} одобрен.", item.getId());
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            log.info("Cтатус бронирования вещи {} отклонен.", item.getId());
            booking.setStatus(BookingStatus.REJECTED);
        }
        log.info("Cтатус бронирования вещи {} передан для сохранения в базе данных с № {}.", item.getId(), bookingId);
        return repository.save(booking);
    }

    @Override
    public Booking findById(Long userId, Long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() ->
                        new NotFoundException("Не найдена вещь для бронирования с идентификатором № " + bookingId));
        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            log.debug("Попытка получения информации о бронировании вещи {} не ее собственником {}.",
                    booking.getItem().getOwner().getId(), userId);
            throw new NotFoundException("Недостаточно прав для получения информации о бронировании вещи " +
                    "с идентификатором № " + bookingId);
        }
        log.info("Передана информация о бронировании вещи {} ее собственнику {}.",
                booking.getItem().getOwner().getId(), userId);
        return booking;
    }

    @Override
    public List<Booking> findAllByBooker(Long userId, String state) {
        userService.findById(userId);
        List<Booking> bookingsList = new ArrayList<>();
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new StateException("Unknown state: UNSUPPORTED_STATUS"); // требование теста
        }
        switch (bookingState) {
            case ALL:
                bookingsList = repository.findAllByBookerId(userId);
                break;
            case PAST:
                bookingsList = repository.findPastByBookerId(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingsList = repository.findFutureByBookerId(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingsList = repository.findCurrentByBookerId(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingsList = repository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingsList = repository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        log.info("Передан список забронированых вещей их арендатору {}.", userId);
        return bookingsList;
    }

    @Override
    public List<Booking> findAllByOwner(Long userId, String state) {
        userService.findById(userId);
        List<Booking> bookingsList = new ArrayList<>();
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new StateException("Unknown state: UNSUPPORTED_STATUS"); // требование теста
        }
        switch (bookingState) {
            case ALL:
                bookingsList = repository.findAllByOwnerId(userId);
                break;
            case PAST:
                bookingsList = repository.findPastByOwnerId(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingsList = repository.findFutureByOwnerId(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingsList = repository.findCurrentByOwnerId(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingsList = repository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingsList = repository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        log.info("Передан список забронированых вещей их собственнику {}.", userId);
        return bookingsList;
    }

    public Optional<Booking> findLastBooking(Long itemId) {
        return repository.findLastBookings(itemId, LocalDateTime.now())
                .stream().findFirst();
    }

    public Optional<Booking> findNextBooking(Long itemId) {
        return repository.findNextBookings(itemId, LocalDateTime.now())
                .stream().findFirst();
    }
}
