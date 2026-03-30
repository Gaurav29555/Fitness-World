package com.fitnessworld.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fitnessworld.auth.api.AuthDtos.RegisterRequest;
import com.fitnessworld.auth.domain.UserCredential;
import com.fitnessworld.auth.domain.UserCredentialRepository;
import com.fitnessworld.platform.security.JwtProperties;
import com.fitnessworld.platform.security.JwtTokenService;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class AuthServiceTest {

    @Test
    void shouldRegisterUser() {
        UserCredentialRepository repository = org.mockito.Mockito.mock(UserCredentialRepository.class);
        when(repository.findByEmailIgnoreCase("test@fitnessworld.local")).thenReturn(Optional.empty());
        when(repository.save(any(UserCredential.class))).thenAnswer(invocation -> {
            UserCredential credential = invocation.getArgument(0);
            credential.setId(99L);
            return credential;
        });

        AuthService service = new AuthService(
                repository,
                new BCryptPasswordEncoder(),
                new JwtTokenService(new JwtProperties("fitness-world", "fitness-world-jwt-secret-should-be-32-bytes-minimum", 60)));

        var response = service.register(new RegisterRequest("test@fitnessworld.local", "Password@123", Set.of("ROLE_MEMBER"), "en", 99L));
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.email()).isEqualTo("test@fitnessworld.local");
    }
}
