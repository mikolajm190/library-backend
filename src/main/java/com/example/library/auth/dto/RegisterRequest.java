package com.example.library.auth.dto;

import org.springframework.lang.NonNull;

public record RegisterRequest(
        @NonNull
        String username,

        @NonNull
        String password
) {
}
