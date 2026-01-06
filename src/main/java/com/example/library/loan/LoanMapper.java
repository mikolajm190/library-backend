package com.example.library.loan;

import com.example.library.book.BookMapper;
import com.example.library.loan.dto.LoanResponse;
import com.example.library.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanMapper {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public LoanResponse toDto(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBorrowDate(),
                loan.getReturnDate(),
                userMapper.toDto(loan.getUser()),
                bookMapper.toDto(loan.getBook())
        );
    }
}
