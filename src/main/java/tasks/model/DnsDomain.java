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
public class DnsDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String domainName;
    private String requester;
    private String description;
    private String aRecord;
    private String registeredDate;
    private boolean sslApplied;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
