package com.example.library.user.dto;

import jakarta.validation.constraints.NotNull;

public record CreateUpdateUserRequest(
        @NotNull
        String username,

        @NotNull
        String password
) {
}
