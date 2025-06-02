package tasks.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"poll_id", "user_id"}))
public class PollVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    @JsonIgnore
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "selected_option_id")
    @JsonIgnore
    private PollOption selectedOption;

    private LocalDateTime votedAt;

    @PrePersist
    public void prePersist() {
        this.votedAt = LocalDateTime.now();
    }
}
