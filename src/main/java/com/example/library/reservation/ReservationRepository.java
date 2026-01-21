package com.example.library.reservation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Optional<Reservation> findByUserIdAndBookId(UUID userId, UUID bookId);
    List<Reservation> findAllByUserId(UUID userId, Pageable pageable);

    void deleteByUserId(UUID userId);
    void deleteByBookId(UUID bookId);

    boolean existsByUserIdAndBookId(UUID userId, UUID bookId);
}
