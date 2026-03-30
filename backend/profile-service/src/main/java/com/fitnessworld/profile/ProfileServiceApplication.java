package com.fitnessworld.profile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "com.fitnessworld")
public class ProfileServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProfileServiceApplication.class, args);
    }
}

@Entity
@Table(name = "member_profiles")
class MemberProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String fullName;
    public String region;
    public String preferredLanguage;
    public String goal;
    public Integer age;
    public Double heightCm;
    public Double weightKg;
}

interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
}

record ProfileRequest(@NotBlank String fullName, @NotBlank String region, @NotBlank String preferredLanguage,
                      @NotBlank String goal, Integer age, Double heightCm, Double weightKg) {
}

@RestController
@RequestMapping("/api/profiles")
class ProfileController {
    private final MemberProfileRepository repository;

    ProfileController(MemberProfileRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<MemberProfile> all() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    MemberProfile byId(@PathVariable Long id, @RequestHeader(value = "X-Auth-Roles", required = false) String roles) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Profile not found for roles=" + roles));
    }

    @PostMapping
    MemberProfile save(@Valid @RequestBody ProfileRequest request) {
        MemberProfile profile = new MemberProfile();
        profile.fullName = request.fullName();
        profile.region = request.region();
        profile.preferredLanguage = request.preferredLanguage();
        profile.goal = request.goal();
        profile.age = request.age();
        profile.heightCm = request.heightCm();
        profile.weightKg = request.weightKg();
        return repository.save(profile);
    }
}
