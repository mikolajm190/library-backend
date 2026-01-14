package com.example.library.loan;

import com.example.library.book.Book;
import com.example.library.book.BookRepository;
import com.example.library.loan.dto.CreateLoanRequest;
import com.example.library.loan.dto.LoanResponse;
import com.example.library.loan.dto.UpdateLoanRequest;
import com.example.library.user.User;
import com.example.library.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanMapper mapper;

    public List<LoanResponse> getAllLoans(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return loanRepository
                .findAll(pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<LoanResponse> getAllLoans(int page, int size, String sortBy, String sortOrder, final UUID userId) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return loanRepository
                .findAllByUserId(userId, pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public LoanResponse getLoan(final UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(EntityNotFoundException::new);
        return mapper.toDto(loan);
    }

    @Transactional
    public LoanResponse createLoan(CreateLoanRequest request) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        User user = userRepository.findById(request.userId())
                .orElseThrow(EntityNotFoundException::new);
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(EntityNotFoundException::new);

        final int bookAvailableCopies = book.getAvailableCopies();
        if (bookAvailableCopies == 0) {
            throw new IllegalStateException("Book is currently unavailable");
        }

        if (loanRepository.existsByBookIdAndUserId(book.getId(), user.getId())) {
            throw new IllegalStateException("You have this book on loan");
        }

        book.setAvailableCopies(bookAvailableCopies - 1);
        book.setCopiesOnLoan(book.getCopiesOnLoan() + 1);
        bookRepository.save(book);

        Loan loan = Loan.builder()
                .borrowDate(currentDateTime)
                .returnDate(currentDateTime.plusMonths(1))
                .user(user)
                .book(book)
                .build();

        loanRepository.save(loan);
        return mapper.toDto(loan);
    }

    public LoanResponse updateLoan(final UUID loanId, UpdateLoanRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(EntityNotFoundException::new);

        loan.setReturnDate(loan.getReturnDate().plusDays(request.daysToProlong()));

        loanRepository.save(loan);
        return mapper.toDto(loan);
    }

    @Transactional
    public void deleteLoan(final UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                        .orElseThrow(EntityNotFoundException::new);
        Book book = bookRepository.findById(loan.getBook().getId())
                        .orElseThrow(EntityNotFoundException::new);

        loanRepository.deleteById(loanId);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setCopiesOnLoan(book.getCopiesOnLoan() - 1);
        bookRepository.save(book);
    }
}
