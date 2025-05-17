package tasks.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dns_record")
public class DnsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String host;

    @Column(nullable = true)
    private String maindomain;

    @Column(nullable = true)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DnsType type;

    @Column(nullable = false)
    private String ip;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean sslValid = false;

    @Column(name = "ssl_expiry_date")
    private LocalDate sslExpiryDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum DnsType {
        A, AAAA, CNAME, MX, NS, TXT, SRV
    }
}
