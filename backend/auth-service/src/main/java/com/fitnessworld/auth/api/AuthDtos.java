package com.fitnessworld.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(@Email String email, @NotBlank String password) {
    }

    public record RegisterRequest(
            @Email String email,
            @NotBlank String password,
            @NotEmpty Set<String> roles,
            @NotBlank String preferredLanguage,
            Long memberId) {
    }

    public record AuthResponse(String accessToken, String email, Set<String> roles, Long memberId, String preferredLanguage) {
    }
}
