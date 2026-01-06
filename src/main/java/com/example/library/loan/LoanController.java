package com.example.library.loan;

import com.example.library.loan.dto.CreateLoanRequest;
import com.example.library.loan.dto.LoanResponse;
import com.example.library.loan.dto.UpdateLoanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable UUID loanId) {
        return ResponseEntity.ok(loanService.getLoan(loanId));
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@RequestBody CreateLoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(request));
    }

    @PutMapping("/{loanId}")
    public ResponseEntity<LoanResponse> updateLoan(
            @PathVariable UUID loanId,
            @RequestBody UpdateLoanRequest request
    ) {
        return ResponseEntity.ok(loanService.updateLoan(loanId, request));
    }

    @DeleteMapping("/{loanId}")
    public ResponseEntity<?> deleteLoan(@PathVariable UUID loanId) {
        loanService.deleteLoan(loanId);
        return ResponseEntity.noContent().build();
    }
}
