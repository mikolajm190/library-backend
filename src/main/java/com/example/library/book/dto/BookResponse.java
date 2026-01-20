package com.example.library.book.dto;

import java.util.UUID;

public record BookResponse(
        UUID id,
        String title,
        String author,
        int totalCopies,
        int availableCopies
) {
}
