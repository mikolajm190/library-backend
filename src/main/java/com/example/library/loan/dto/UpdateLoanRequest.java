package com.example.library.loan.dto;

import jakarta.validation.constraints.Min;

public record UpdateLoanRequest(
        @Min(1)
        int daysToProlong
) {
}
