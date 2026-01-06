package com.example.library.loan.dto;

import java.util.UUID;

public record CreateLoanRequest(
        UUID userId,
        UUID bookId
) {
}
