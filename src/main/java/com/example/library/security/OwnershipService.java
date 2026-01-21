package com.example.library.security;

import com.example.library.loan.LoanRepository;
import com.example.library.reservation.ReservationRepository;
import com.example.library.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("ownership")
@RequiredArgsConstructor
public class OwnershipService {

    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;

    public boolean isLoanOwner(User principal, UUID loanId) {
        return loanRepository.findById(loanId)
                .map(loan -> loan.getUser().getId().equals(principal.getId()))
                .orElse(false);
    }

    public boolean isProfileOwner(User principal, UUID userId) {
        return principal.getId().equals(userId);
    }

    public boolean isReservationOwner(User principal, UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> reservation.getUser().getId().equals(principal.getId()))
                .orElse(false);
    }
}
