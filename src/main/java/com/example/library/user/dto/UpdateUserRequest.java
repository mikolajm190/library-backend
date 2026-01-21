package com.example.library.user.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotNull
        String username,

        @NotNull
        String password
) {
}
