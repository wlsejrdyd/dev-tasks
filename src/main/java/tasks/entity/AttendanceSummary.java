package tasks.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "attendance_summary", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "year", "month"})
})
public class AttendanceSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int year;
    private int month;

    private double annualGiven;
    private double annualUsed;

    private double dayoffGiven;
    private double dayoffUsed;

    private LocalDateTime createdAt;
}
