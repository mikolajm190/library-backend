package com.example.library.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    Optional<Book> findByTitle(String title);

    @Modifying
    @Query("UPDATE Book b SET b.availableCopies = b.availableCopies + 1, b.copiesOnLoan = b.copiesOnLoan - 1 WHERE b.id IN (SELECT l.book.id FROM Loan l WHERE l.user.id = :userId)")
    void updateCountersOnReturn(@Param("userId") UUID userId);
}
