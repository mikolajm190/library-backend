package com.example.library.user.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username
) {
}
