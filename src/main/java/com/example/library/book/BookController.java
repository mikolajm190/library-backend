package com.example.library.book;

import com.example.library.book.dto.BookResponse;
import com.example.library.book.dto.CreateBookRequest;
import com.example.library.book.dto.UpdateBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(@PathVariable UUID bookId) {
        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody CreateBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable UUID bookId,
            @RequestBody UpdateBookRequest request
    ) {
        return ResponseEntity.ok(bookService.updateBook(bookId, request));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable UUID bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
