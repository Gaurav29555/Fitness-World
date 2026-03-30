package com.fitnessworld.auth.application;

import com.fitnessworld.auth.api.AuthDtos.AuthResponse;
import com.fitnessworld.auth.api.AuthDtos.LoginRequest;
import com.fitnessworld.auth.api.AuthDtos.RegisterRequest;
import com.fitnessworld.auth.domain.UserCredential;
import com.fitnessworld.auth.domain.UserCredentialRepository;
import com.fitnessworld.platform.security.JwtTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserCredentialRepository repository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        repository.findByEmailIgnoreCase(request.email()).ifPresent(existing -> {
            throw new IllegalArgumentException("User already exists");
        });

        UserCredential credential = new UserCredential();
        credential.setEmail(request.email());
        credential.setPasswordHash(passwordEncoder.encode(request.password()));
        credential.setMemberId(request.memberId());
        credential.setPreferredLanguage(request.preferredLanguage());
        credential.getRoles().addAll(request.roles());
        UserCredential saved = repository.save(credential);
        return tokenFor(saved);
    }

    public AuthResponse login(LoginRequest request) {
        UserCredential credential = repository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), credential.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return tokenFor(credential);
    }

    private AuthResponse tokenFor(UserCredential credential) {
        String token = jwtTokenService.generateToken(
                credential.getEmail(),
                credential.getRoles(),
                credential.getMemberId(),
                credential.getPreferredLanguage());
        return new AuthResponse(token, credential.getEmail(), credential.getRoles(), credential.getMemberId(), credential.getPreferredLanguage());
    }
}
