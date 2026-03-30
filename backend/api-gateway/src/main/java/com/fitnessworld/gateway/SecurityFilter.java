package com.fitnessworld.gateway;

import com.fitnessworld.platform.security.JwtTokenService;
import io.jsonwebtoken.Claims;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityFilter implements GlobalFilter, Ordered {

    private final JwtTokenService jwtTokenService;

    public SecurityFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/api/auth/") || path.startsWith("/actuator") || path.startsWith("/fallback/")) {
            return chain.filter(exchange);
        }

        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Claims claims = jwtTokenService.parse(header.substring(7));
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-Auth-Subject", claims.getSubject())
                    .header("X-Auth-Roles", String.join(",", claims.get("roles", List.class)))
                    .header("X-Auth-MemberId", String.valueOf(((Number) claims.get("memberId")).longValue()))
                    .header("X-Auth-Language", String.valueOf(claims.get("language", String.class)))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (Exception ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

