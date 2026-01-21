package com.example.library.reservation.dto;

import com.example.library.book.dto.BookResponse;
import com.example.library.reservation.constant.ReservationStatus;
import com.example.library.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        ReservationStatus status,
        UserResponse user,
        BookResponse book
) {
}
