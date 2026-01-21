package com.example.library.reservation;

import com.example.library.reservation.constant.ReservationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Optional<Reservation> findByUserIdAndBookId(UUID userId, UUID bookId);
    List<Reservation> findAllByUserId(UUID userId, Pageable pageable);

    @Query("SELECT DISTINCT r.bookId FROM Reservations r WHERE r.status = :status AND r.expiresAt < :now")
    List<UUID> findBookIdsWithExpiredReservations(@Param("status") ReservationStatus status, @Param("now") LocalDateTime now);

    @Modifying
    @Query(value = """
            UPDATE reservation
            SET status = 'READY'
                expires_at = :newExpiry
            WHERE id IN (
                SELECT r.id
                FROM reservation r
                WHERE r.book_id = :bookId
                AND r.status = 'QUEUED'
                ORDER BY r.createdAt ASC
                LIMIT :limit
                )
            """, nativeQuery = true)
    int updateStatusForQueuedReservations(@Param("bookId") UUID bookId,
                                           @Param("limit") int limit,
                                           @Param("newExpiry") LocalDateTime newExpiry);

    @Modifying
    @Query("DELETE FROM Reservations r WHERE r.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Reservations r WHERE r.bookId = :bookId")
    void deleteByBookId(@Param("bookId") UUID bookId);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.status = :status AND r.expiresAt < :now")
    void deleteExpiredReservations(@Param("status") ReservationStatus status, @Param("now") LocalDateTime now);

    boolean existsByUserIdAndBookId(UUID userId, UUID bookId);
}
