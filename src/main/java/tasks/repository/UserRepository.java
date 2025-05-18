package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);         // 로그인용
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // ✅ 이름으로 사용자 조회 (근태 업로드용)
    Optional<User> findByName(String name);
}
