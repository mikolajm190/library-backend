package com.example.library.reservation;

import com.example.library.book.Book;
import com.example.library.book.BookRepository;
import com.example.library.reservation.constant.ReservationStatus;
import com.example.library.reservation.dto.CreateReservationRequest;
import com.example.library.reservation.dto.ReservationResponse;
import com.example.library.user.User;
import com.example.library.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReservationMapper mapper;

    public List<ReservationResponse> getAllReservations(final int page, final int size, final String sortBy, final String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return reservationRepository
                .findAll(pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<ReservationResponse> getAllReservations(final int page, final int size, final String sortBy, final String sortOrder, final UUID userId) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return reservationRepository
                .findAllByUserId(userId, pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public ReservationResponse getReservation(final UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(EntityNotFoundException::new);
        return mapper.toDto(reservation);
    }

    public ReservationResponse createReservation(final CreateReservationRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(EntityNotFoundException::new);
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(EntityNotFoundException::new);

        if (reservationRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new IllegalStateException("User has a pending reservation for this book");
        }

        LocalDateTime currentTimestamp = LocalDateTime.now();
        boolean bookHasAvailableCopies = book.getAvailableCopies() > 0;
        LocalDateTime expiryTimestamp = bookHasAvailableCopies
                ? currentTimestamp.plusDays(3)
                : currentTimestamp.plusMonths(3);
        ReservationStatus status = bookHasAvailableCopies
                ? ReservationStatus.READY
                : ReservationStatus.QUEUED;

        Reservation reservation = Reservation.builder()
                .createdAt(currentTimestamp)
                .expiresAt(expiryTimestamp)
                .status(status)
                .build();

        reservationRepository.save(reservation);
        return mapper.toDto(reservation);
    }

    public void deleteReservation(final UUID reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new EntityNotFoundException("Resource not found");
        }
        reservationRepository.deleteById(reservationId);
    }

    @Transactional
    public void deleteExpiredReservations() {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        reservationRepository.deleteExpiredReservations(ReservationStatus.QUEUED, currentTimestamp);
        final List<UUID> bookIds = reservationRepository.findBookIdsWithExpiredReservations(ReservationStatus.READY, currentTimestamp);
        reservationRepository.deleteExpiredReservations(ReservationStatus.READY, currentTimestamp);

        for (UUID bookId: bookIds) {
            processBookQueue(bookId, currentTimestamp);
        }
    }

    private int processBookQueue(final UUID bookId, final LocalDateTime currentTimestamp) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(EntityNotFoundException::new);

        return reservationRepository.updateStatusForQueuedReservations(bookId, book.getAvailableCopies(), currentTimestamp);
    }
}
