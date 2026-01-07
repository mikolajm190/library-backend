package com.example.library.loan.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLoanRequest(
        @NotNull
        UUID userId,

        @NotNull
        UUID bookId
) {
}
