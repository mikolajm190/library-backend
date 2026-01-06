package com.example.library.book.dto;

public record UpdateBookRequest(
        String title,
        String author
) {
}
