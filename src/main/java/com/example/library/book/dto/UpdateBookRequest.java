package com.example.library.book.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateBookRequest(
        @NotNull
        String title,

        @NotNull
        String author
) {
}
