package com.example.library.loan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLoanRequest(
        @NotBlank
        UUID userId,

        @NotNull
        UUID bookId
) {
}
