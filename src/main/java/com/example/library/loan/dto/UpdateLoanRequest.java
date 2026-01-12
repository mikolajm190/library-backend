package com.example.library.loan.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateLoanRequest(
        @Min(1)
        @Max(30)
        int daysToProlong
) {
}
