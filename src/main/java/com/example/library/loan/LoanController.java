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
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "returnDate") @Pattern(regexp = "returnDate") String sortBy,
            @RequestParam(defaultValue = "desc") @Pattern(regexp = "ASC|DESC", flags = Pattern.Flag.CASE_INSENSITIVE) String sortOrder,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return ResponseEntity.ok(loanService.getAllLoans(page, size, sortBy, sortOrder, currentUser.getId()));
        }
        return ResponseEntity.ok(loanService.getAllLoans(page, size, sortBy, sortOrder));
    }

    @GetMapping("/{loanId}")
    @PreAuthorize("hasRole('ADMIN') or @ownership.isLoanOwner(principal, #loanId)")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable UUID loanId) {
        return ResponseEntity.ok(loanService.getLoan(loanId));
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(
            @Valid @RequestBody CreateLoanRequest request,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin || request.userId() == null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(
                    new CreateLoanRequest(
                            currentUser.getId(),
                            request.bookId()
                    )
            ));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(request));
    }

    @PutMapping("/{loanId}")
    @PreAuthorize("hasRole('ADMIN') or @ownership.isLoanOwner(principal, #loanId)")
    public ResponseEntity<LoanResponse> updateLoan(
            @PathVariable UUID loanId,
            @Valid @RequestBody UpdateLoanRequest request
    ) {
        return ResponseEntity.ok(loanService.updateLoan(loanId, request));
    }

    @DeleteMapping("/{loanId}")
    @PreAuthorize("hasRole('ADMIN') or @ownership.isLoanOwner(principal, #loanId)")
    public ResponseEntity<?> deleteLoan(@PathVariable UUID loanId) {
        loanService.deleteLoan(loanId);
        return ResponseEntity.noContent().build();
    }
}
