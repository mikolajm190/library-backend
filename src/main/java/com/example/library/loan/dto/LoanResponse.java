package com.example.library.loan.dto;

import com.example.library.book.dto.BookResponse;
import com.example.library.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        LocalDateTime borrowDate,
        LocalDateTime returnDate,
        UserResponse user,
        BookResponse book
) {
}
