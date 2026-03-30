package com.fitnessworld.billing;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class BillingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }
}

enum PaymentProvider { STRIPE, RAZORPAY, UPI }
enum PaymentStatus { PENDING, PAID, FAILED }

@Entity
@Table(name = "payment_records")
class PaymentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long memberId;
    public String planName;
    @Enumerated(EnumType.STRING)
    public PaymentProvider provider;
    @Enumerated(EnumType.STRING)
    public PaymentStatus status;
    public String invoiceNumber;
    public Double amount;
    public String currency;
    public Instant createdAt = Instant.now();
    public String externalReference;
}

interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    List<PaymentRecord> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}

record PaymentRequest(Long memberId, @NotBlank String planName, @NotBlank String provider, Double amount, String currency) {}
record PaymentResponse(Long paymentId, String status, String checkoutUrl, String invoiceNumber) {}

@RestController
@RequestMapping("/api/billing")
class BillingController {
    private final PaymentRecordRepository repository;

    BillingController(PaymentRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/payments")
    List<PaymentRecord> all() {
        return repository.findAll();
    }

    @PostMapping("/checkout")
    PaymentResponse checkout(@Valid @RequestBody PaymentRequest request) {
        PaymentRecord record = new PaymentRecord();
        record.memberId = request.memberId();
        record.planName = request.planName();
        record.provider = PaymentProvider.valueOf(request.provider().toUpperCase());
        record.status = PaymentStatus.PENDING;
        record.amount = request.amount();
        record.currency = request.currency() == null ? "INR" : request.currency();
        record.invoiceNumber = "INV-" + System.currentTimeMillis();
        record.externalReference = record.provider.name() + "-SESSION-" + System.nanoTime();
        PaymentRecord saved = repository.save(record);
        String checkoutUrl = switch (saved.provider) {
            case STRIPE -> "/mock/stripe/checkout/" + saved.id;
            case RAZORPAY -> "/mock/razorpay/checkout/" + saved.id;
            case UPI -> "upi://pay?pa=fitnessworld@upi&pn=FitnessWorld&am=" + saved.amount;
        };
        return new PaymentResponse(saved.id, saved.status.name(), checkoutUrl, saved.invoiceNumber);
    }

    @GetMapping(value = "/payments/export/csv", produces = "text/csv")
    ResponseEntity<byte[]> exportCsv() {
        StringBuilder csv = new StringBuilder("invoiceNumber,memberId,provider,status,amount,currency,createdAt\n");
        repository.findAll().forEach(payment -> csv.append(payment.invoiceNumber).append(',').append(payment.memberId).append(',').append(payment.provider).append(',').append(payment.status).append(',').append(payment.amount).append(',').append(payment.currency).append(',').append(payment.createdAt).append('\n'));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payments.csv")
                .contentType(new MediaType("text", "csv")).body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/invoices")
    List<PaymentRecord> invoices(@RequestHeader(value = "X-Auth-MemberId", required = false) Long memberId) {
        return memberId == null ? repository.findAll() : repository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }
}
