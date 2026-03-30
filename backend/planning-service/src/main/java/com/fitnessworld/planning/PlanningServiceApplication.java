package com.fitnessworld.planning;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication(scanBasePackages = "com.fitnessworld")
public class PlanningServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlanningServiceApplication.class, args);
    }
}

enum PlanType { WORKOUT, NUTRITION }

@Entity
@Table(name = "plan_snapshots")
class PlanSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long memberId;
    @Enumerated(EnumType.STRING)
    public PlanType type;
    public Integer versionNo;
    public String language;
    public String region;
    public String goal;
    @jakarta.persistence.Column(columnDefinition = "TEXT")
    public String content;
    public Instant createdAt = Instant.now();
}

interface PlanSnapshotRepository extends JpaRepository<PlanSnapshot, Long> {
    List<PlanSnapshot> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}

record PlanRequest(Long memberId, @NotBlank String type, @NotBlank String goal, @NotBlank String region, @NotBlank String language,
                   Double heightCm, Double weightKg, Integer age) {}

record AiPlanResponse(String content) {}

@RestController
@RequestMapping("/api/plans")
class PlanController {
    private final PlanSnapshotRepository repository;
    private final WebClient webClient = WebClient.builder().baseUrl(System.getenv().getOrDefault("AI_SERVICE_URL", "http://localhost:8087")).build();

    PlanController(PlanSnapshotRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<PlanSnapshot> all() {
        return repository.findAll();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    PlanSnapshot generate(@Valid @RequestBody PlanRequest request) {
        String generated = null;
        try {
            generated = webClient.post().uri("/api/ai/plan")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AiPlanResponse.class)
                    .map(AiPlanResponse::content)
                    .block();
        } catch (Exception ignored) {
        }
        List<PlanSnapshot> existing = repository.findByMemberIdOrderByCreatedAtDesc(request.memberId());
        PlanSnapshot snapshot = new PlanSnapshot();
        snapshot.memberId = request.memberId();
        snapshot.type = PlanType.valueOf(request.type().toUpperCase());
        snapshot.versionNo = existing.isEmpty() ? 1 : existing.get(0).versionNo + 1;
        snapshot.language = request.language();
        snapshot.region = request.region();
        snapshot.goal = request.goal();
        snapshot.content = generated != null ? generated : "Focus: " + request.goal() + "; language=" + request.language() + "; region=" + request.region();
        return repository.save(snapshot);
    }
}
