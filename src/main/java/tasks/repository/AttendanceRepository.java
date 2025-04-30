package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.model.Attendance;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findTop5ByOrderByWorkDateDesc();
}
