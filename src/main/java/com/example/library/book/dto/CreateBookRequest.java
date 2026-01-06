package com.example.library.book.dto;

public record CreateBookRequest(
        String title,
        String author,
        int availableCopies
) {
}
