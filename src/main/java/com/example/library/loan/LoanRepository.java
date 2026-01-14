package com.example.library.loan;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    List<Loan> findAllByUserId(UUID userId, Pageable pageable);

    void deleteByUserId(UUID userId);
    void deleteByBookId(UUID bookId);

    boolean existsByBookIdAndUserId(UUID bookId, UUID userId);
}
