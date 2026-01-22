package com.example.library.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReservationRequest(
        @NotNull
        UUID userId,

        @NotNull
        UUID bookId
) {
}
