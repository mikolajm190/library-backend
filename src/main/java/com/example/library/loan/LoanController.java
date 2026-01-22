package com.example.library.loan;

import com.example.library.loan.dto.CreateLoanRequest;
import com.example.library.loan.dto.LoanResponse;
import com.example.library.loan.dto.UpdateLoanRequest;
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
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans(
            @RequestParam(defaultValue = "0") @Min(0) final int page,
            @RequestParam(defaultValue = "10") @Min(1) final int size,
            @RequestParam(defaultValue = "returnDate") @Pattern(regexp = "returnDate") final String sortBy,
            @RequestParam(defaultValue = "desc") @Pattern(regexp = "ASC|DESC", flags = Pattern.Flag.CASE_INSENSITIVE) final String sortOrder,
            final Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        boolean canReadOthersLoans = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())
                                                            || "ROLE_LIBRARIAN".equals(a.getAuthority()));
        if (!canReadOthersLoans) {
            return ResponseEntity.ok(loanService.getAllLoans(page, size, sortBy, sortOrder, currentUser.getId()));
        }
        return ResponseEntity.ok(loanService.getAllLoans(page, size, sortBy, sortOrder));
    }

    @GetMapping("/{loanId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or @ownership.isLoanOwner(principal, #loanId)")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable final UUID loanId) {
        return ResponseEntity.ok(loanService.getLoan(loanId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody final CreateLoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(request));
    }

    @PutMapping("/{loanId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or @ownership.isLoanOwner(principal, #loanId)")
    public ResponseEntity<LoanResponse> updateLoan(
            @PathVariable final UUID loanId,
            @Valid @RequestBody final UpdateLoanRequest request
    ) {
        return ResponseEntity.ok(loanService.updateLoan(loanId, request));
    }

    @DeleteMapping("/{loanId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<?> deleteLoan(@PathVariable final UUID loanId) {
        loanService.deleteLoan(loanId);
        return ResponseEntity.noContent().build();
    }
}
