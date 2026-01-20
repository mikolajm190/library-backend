package com.example.library.book;

import com.example.library.book.dto.BookResponse;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookResponse toDto(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
}
