package com.fitnessworld.ai;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication(scanBasePackages = "com.fitnessworld")
public class AiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
    }
}

record ChatRequest(@NotBlank String language, @NotBlank String region, @NotBlank String prompt) {}
record PlanRequest(Long memberId, @NotBlank String type, @NotBlank String goal, @NotBlank String region, @NotBlank String language,
                   Double heightCm, Double weightKg, Integer age) {}
record AiOutput(String content) {}

@RestController
@RequestMapping("/api/ai")
class AiController {
    private final WebClient webClient;
    private final String model;

    AiController(@Value("${ai.base-url:https://api.groq.com/openai/v1}") String baseUrl,
                 @Value("${ai.api-key:}") String apiKey,
                 @Value("${ai.model:llama-3.3-70b-versatile}") String model) {
        this.model = model;
        this.webClient = WebClient.builder().baseUrl(baseUrl).defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey).build();
    }

    @PostMapping("/chat")
    AiOutput chat(@Valid @RequestBody ChatRequest request) {
        String systemPrompt = "Respond as a supportive gym coach in " + request.language() + " for users in " + request.region() + ". Keep it practical, safe, and motivational.";
        return new AiOutput(generate(systemPrompt, request.prompt()));
    }

    @PostMapping("/plan")
    AiOutput plan(@Valid @RequestBody PlanRequest request) {
        String prompt = "Generate a concise " + request.type() + " plan in " + request.language() + " for goal " + request.goal() + ". Include regional meals for " + request.region() + ". Weight=" + request.weightKg() + ", height=" + request.heightCm() + ", age=" + request.age();
        return new AiOutput(generate("You are a multilingual fitness planner.", prompt));
    }

    private String generate(String system, String prompt) {
        try {
            Map body = Map.of(
                    "model", model,
                    "messages", List.of(Map.of("role", "system", "content", system), Map.of("role", "user", "content", prompt))
            );
            Map response = webClient.post().uri("/chat/completions").contentType(MediaType.APPLICATION_JSON).bodyValue(body)
                    .retrieve().bodyToMono(Map.class).block();
            List choices = (List) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map message = (Map) ((Map) choices.get(0)).get("message");
                return String.valueOf(message.get("content"));
            }
        } catch (Exception ignored) {
        }
        return prompt + "\n\nFallback: Groq-compatible provider not configured, so this placeholder response was returned.";
    }
}
