package com.fitnessworld.notification;

import java.time.Instant;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "com.fitnessworld")
@EnableScheduling
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/notifications")
class NotificationController {
    @GetMapping("/status")
    Map<String, Object> status() {
        return Map.of("service", "notification-service", "scheduler", "enabled", "timestamp", Instant.now());
    }
}

@org.springframework.stereotype.Component
class ReminderScheduler {
    @Scheduled(cron = "0 0 8 * * *")
    void dailyReminders() {
        System.out.println("Dispatching daily habit reminders at " + Instant.now());
    }

    @Scheduled(cron = "0 0 9 * * MON")
    void weeklySummary() {
        System.out.println("Generating weekly summaries at " + Instant.now());
    }
}
