package com.example.library.user.dto;

import com.example.library.user.constants.UserRole;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotNull
        String username,

        @NotNull
        String password,

        @NotNull
        UserRole role
) {
}
