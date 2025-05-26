package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.AttendanceStatus;
import tasks.entity.User;

import java.util.Optional;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceStatus, Long> {
    Optional<AttendanceStatus> findByUser(User user);
}
