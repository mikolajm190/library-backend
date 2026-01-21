package com.example.library.reservation;

import com.example.library.reservation.dto.CreateReservationRequest;
import com.example.library.reservation.dto.ReservationResponse;
import com.example.library.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations(
            @RequestParam(defaultValue = "0") @Min(0) final int page,
            @RequestParam(defaultValue = "10") @Min(1) final int size,
            @RequestParam(defaultValue = "createdAt") @Pattern(regexp = "createdAt") final String sortBy,
            @RequestParam(defaultValue = "desc") @Pattern(regexp = "ASC|DESC", flags = Pattern.Flag.CASE_INSENSITIVE) final String sortOrder,
            final Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        boolean canReadOthersReservations = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())
                                                        || "ROLE_LIBRARIAN".equals(a.getAuthority()));
        if (!canReadOthersReservations) {
            return ResponseEntity.ok(reservationService.getAllReservations(page, size, sortBy, sortOrder, currentUser.getId()));
        }
        return ResponseEntity.ok(reservationService.getAllReservations(page, size, sortBy, sortOrder));
    }

    @GetMapping("/{reservationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or @ownership.isReservationOwner(principal, #reservationId)")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable final UUID loanId) {
        return ResponseEntity.ok(reservationService.getReservation(loanId));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody final CreateReservationRequest request,
            final Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        boolean canModifyOthersReservations = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())
                                                            || "ROLE_LIBRARIAN".equals(a.getAuthority()));
        if (!canModifyOthersReservations || !currentUser.getId().equals(request.userId())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(
                    new CreateReservationRequest(
                            currentUser.getId(),
                            request.bookId()
                    )
            ));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(request));
    }

    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or @ownership.isReservationOwner(principal, #reservationId)")
    public ResponseEntity<?> deleteReservation(@PathVariable final UUID loanId) {
        reservationService.deleteReservation(loanId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/expired")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> deleteExpiredReservations() {
        reservationService.deleteExpiredReservations();
        return ResponseEntity.noContent().build();
    }
}
