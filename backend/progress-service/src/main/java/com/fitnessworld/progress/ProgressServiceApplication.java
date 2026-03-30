package com.fitnessworld.progress;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "com.fitnessworld")
public class ProgressServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProgressServiceApplication.class, args);
    }
}

@Entity
@Table(name = "progress_logs")
class ProgressLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long memberId;
    public Instant loggedAt = Instant.now();
    public Double weightKg;
    public Double bodyFat;
    public Double calories;
    public Double proteinGrams;
    public Double squatKg;
    public Double benchKg;
    public Integer complianceScore;
}

interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {
    List<ProgressLog> findByMemberIdOrderByLoggedAtDesc(Long memberId);
}

record ProgressRequest(Long memberId, Double weightKg, Double bodyFat, Double calories, Double proteinGrams,
                       Double squatKg, Double benchKg, Integer complianceScore) {}

record ProgressSummary(Long memberId, int entries, String trend, String correction) {}

@RestController
@RequestMapping("/api/progress")
class ProgressController {
    private final ProgressLogRepository repository;

    ProgressController(ProgressLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<ProgressLog> all() {
        return repository.findAll();
    }

    @PostMapping
    ProgressLog create(@Valid @RequestBody ProgressRequest request) {
        ProgressLog log = new ProgressLog();
        log.memberId = request.memberId();
        log.weightKg = request.weightKg();
        log.bodyFat = request.bodyFat();
        log.calories = request.calories();
        log.proteinGrams = request.proteinGrams();
        log.squatKg = request.squatKg();
        log.benchKg = request.benchKg();
        log.complianceScore = request.complianceScore();
        return repository.save(log);
    }

    @GetMapping("/summary")
    ProgressSummary summary(@RequestHeader("X-Auth-MemberId") Long memberId) {
        List<ProgressLog> logs = repository.findByMemberIdOrderByLoggedAtDesc(memberId);
        String trend = logs.size() > 1 && logs.get(0).weightKg != null && logs.get(1).weightKg != null && logs.get(0).weightKg < logs.get(1).weightKg
                ? "Improving" : "Plateau";
        String correction = "Plateau".equals(trend) ? "Increase training intensity by 5% and tighten macros." : "Continue current progression.";
        return new ProgressSummary(memberId, logs.size(), trend, correction);
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    ResponseEntity<byte[]> exportCsv(@RequestHeader("X-Auth-MemberId") Long memberId) {
        StringBuilder csv = new StringBuilder("loggedAt,weightKg,bodyFat,calories,proteinGrams,complianceScore\n");
        repository.findByMemberIdOrderByLoggedAtDesc(memberId).forEach(log -> csv.append(log.loggedAt).append(',').append(log.weightKg).append(',').append(log.bodyFat).append(',').append(log.calories).append(',').append(log.proteinGrams).append(',').append(log.complianceScore).append('\n'));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=progress.csv")
                .contentType(new MediaType("text", "csv")).body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}
