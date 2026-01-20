package com.example.library.book.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateBookRequest(
        @NotNull
        String title,

        @NotNull
        String author,

        @Min(1)
        @Max(10)
        int totalCopies
) {
}
