package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDateTime end, LocalDateTime start);
}
