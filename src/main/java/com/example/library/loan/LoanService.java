package com.example.library.loan;

import com.example.library.book.Book;
import com.example.library.book.BookRepository;
import com.example.library.loan.dto.CreateLoanRequest;
import com.example.library.loan.dto.LoanResponse;
import com.example.library.loan.dto.UpdateLoanRequest;
import com.example.library.reservation.Reservation;
import com.example.library.reservation.ReservationRepository;
import com.example.library.reservation.constant.ReservationStatus;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final LoanMapper mapper;

    public List<LoanResponse> getAllLoans(final int page, final int size, final String sortBy, final String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return loanRepository
                .findAll(pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<LoanResponse> getAllLoans(final int page, final int size, final String sortBy, final String sortOrder, final UUID userId) {
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
    public LoanResponse createLoan(final CreateLoanRequest request) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        User user = userRepository.findById(request.userId())
                .orElseThrow(EntityNotFoundException::new);
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(EntityNotFoundException::new);
        Optional<Reservation> reservation = reservationRepository.findByUserIdAndBookId(user.getId(), book.getId());

        if (loanRepository.existsByBookIdAndUserId(book.getId(), user.getId())) {
            throw new IllegalStateException("User has this book on loan");
        }

        if (reservation.isPresent()) {
            if (reservation.get().getStatus() != ReservationStatus.READY) {
                throw new IllegalStateException("User reservation is not ready");
            }
        } else {
            if (book.getAvailableCopies() == 0) {
                throw new IllegalStateException("User has no reservation and book is not available");
            }
        }

        Loan loan = Loan.builder()
                .borrowDate(currentDateTime)
                .returnDate(currentDateTime.plusMonths(1))
                .user(user)
                .book(book)
                .build();

        reservation.ifPresent(r -> reservationRepository.deleteById(r.getId()));
        loanRepository.save(loan);
        return mapper.toDto(loan);
    }

    public LoanResponse updateLoan(final UUID loanId, final UpdateLoanRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(EntityNotFoundException::new);

        loan.setReturnDate(loan.getReturnDate().plusDays(request.daysToProlong()));

        loanRepository.save(loan);
        return mapper.toDto(loan);
    }

    public void deleteLoan(final UUID loanId) {
        if (!loanRepository.existsById(loanId)) {
            throw new EntityNotFoundException("Resource not found");
        }
        loanRepository.deleteById(loanId);
    }
}
