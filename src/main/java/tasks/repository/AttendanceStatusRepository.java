package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasks.entity.AttendanceStatus;
import tasks.entity.User;

import java.util.Optional;

@Repository
public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatus, Long> {

    Optional<AttendanceStatus> findByUser(User user);

    boolean existsByUser(User user);

    void deleteByUser(User user); // ✅ 새로 추가
}
