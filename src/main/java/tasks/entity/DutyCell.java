package tasks.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "duty_cell")
public class DutyCell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;
    private int month;

    private int weekIndex; // 0 ~ 4 (최대 5주)
    private int weekdayIndex; // 0 ~ 6 (일~토)

    private String time;   // 주간 근무, 야간 근무 등
    private String name;   // 사람 이름
}

