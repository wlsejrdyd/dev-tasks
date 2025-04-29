package tasks.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String assignedTo;
    private LocalDateTime dueDate;
    private String createdBy;
    private String status; // Incomplete or Complete
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
