package com.example.library.book;

import com.example.library.book.dto.BookResponse;
import com.example.library.book.dto.CreateBookRequest;
import com.example.library.book.dto.UpdateBookRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper mapper;

    public List<BookResponse> getAllBooks() {
        return bookRepository
                .findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public BookResponse getBook(final UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(EntityNotFoundException::new);
        return mapper.toDto(book);
    }

    public BookResponse createBook(CreateBookRequest request) {
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .availableCopies(request.availableCopies())
                .build();

        bookRepository.save(book);
        return mapper.toDto(book);
    }

    public BookResponse updateBook(final UUID bookId, UpdateBookRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(EntityNotFoundException::new);

        book.setTitle(request.title());
        book.setAuthor(request.author());

        bookRepository.save(book);
        return mapper.toDto(book);
    }

    public void deleteBook(final UUID bookId) {
        bookRepository.deleteById(bookId);
    }
}
