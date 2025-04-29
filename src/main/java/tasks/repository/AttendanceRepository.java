package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.model.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
