package com.example.library.user.dto;

public record CreateUpdateUserRequest(
        String username,
        String password
) {
}
