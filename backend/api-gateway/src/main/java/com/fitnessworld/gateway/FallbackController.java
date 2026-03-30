package com.fitnessworld.gateway;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/profile")
    public Map<String, Object> profileFallback() {
        return Map.of("message", "Profile service temporarily unavailable", "status", "DEGRADED");
    }
}
