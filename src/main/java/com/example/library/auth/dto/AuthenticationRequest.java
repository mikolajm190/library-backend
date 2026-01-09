package com.example.library.auth.dto;

import org.springframework.lang.NonNull;

public record AuthenticationRequest(
        @NonNull
        String username,

        @NonNull
        String password
) {
}
