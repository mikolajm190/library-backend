package com.example.library.user.dto;

import com.example.library.user.constants.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        Role role
) {
}
