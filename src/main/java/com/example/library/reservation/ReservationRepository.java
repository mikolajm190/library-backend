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

    @Query("SELECT DISTINCT r.book.id FROM Reservation r WHERE r.status = 'EXPIRED' AND r.expiresAt < current_timestamp")
    List<UUID> findBookIdsWithExpiredReadyReservations();

    @Modifying
    @Query(value = """
            UPDATE reservation
            SET status = 'READY',
                expires_at = :newExpiry
            WHERE id IN (
                SELECT r.id
                FROM reservation r
                WHERE r.book_id = :bookId
                AND r.status = 'QUEUED'
                ORDER BY r.created_at ASC
                LIMIT :limit
                )
            """, nativeQuery = true)
    int updateStatusForQueuedReservations(@Param("bookId") UUID bookId,
                                           @Param("limit") int limit,
                                           @Param("newExpiry") LocalDateTime newExpiry);

    @Modifying
    @Query("UPDATE Reservation r SET r.status = 'EXPIRED' WHERE r.expiresAt < current_timestamp")
    int updateStatusForExpiredReservations();

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.book.id = :bookId")
    void deleteByBookId(@Param("bookId") UUID bookId);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.status = :status AND r.expiresAt < current_timestamp")
    void deleteReservationsByStatus(@Param("status") ReservationStatus status);

    boolean existsByUserIdAndBookId(UUID userId, UUID bookId);
}
