package tasks.entity;

import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private LocalDate joinDate;

    private LocalDate leaveDate;

    @Builder.Default
    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal annualGranted = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal annualUsed = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal compensatoryGranted = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal compensatoryUsed = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
