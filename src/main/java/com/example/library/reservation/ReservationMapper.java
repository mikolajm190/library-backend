package com.example.library.reservation;

import com.example.library.book.BookMapper;
import com.example.library.reservation.dto.ReservationResponse;
import com.example.library.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public ReservationResponse toDto(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getCreatedAt(),
                reservation.getExpiresAt(),
                reservation.getStatus(),
                userMapper.toDto(reservation.getUser()),
                bookMapper.toDto(reservation.getBook())
        );
    }
}
