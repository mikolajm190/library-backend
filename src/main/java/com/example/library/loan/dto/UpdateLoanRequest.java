package com.example.library.loan.dto;

import java.util.UUID;

public record UpdateLoanRequest(
        int daysToProlong
) {
}
