package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStartDateBetween(LocalDate start, LocalDate end);
}
