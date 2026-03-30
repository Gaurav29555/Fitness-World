package com.fitnessworld.auth.api;

import com.fitnessworld.auth.api.AuthDtos.AuthResponse;
import com.fitnessworld.auth.api.AuthDtos.LoginRequest;
import com.fitnessworld.auth.api.AuthDtos.RegisterRequest;
import com.fitnessworld.auth.application.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/health")
    public Map<String, Object> health(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return Map.of("service", "auth-service", "authorizationPresent", authorization != null);
    }
}
