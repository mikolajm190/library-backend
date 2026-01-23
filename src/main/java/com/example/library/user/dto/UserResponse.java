package com.example.library.user.dto;

import com.example.library.user.constants.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        UserRole role
) {
}
