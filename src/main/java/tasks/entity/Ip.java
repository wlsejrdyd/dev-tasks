package tasks.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "ips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private String description;

    private String status; // "UP", "DOWN"
}
