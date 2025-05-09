package tasks.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    private String email;

    @Column(nullable = false)
    private String password;
    private String name;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
}
