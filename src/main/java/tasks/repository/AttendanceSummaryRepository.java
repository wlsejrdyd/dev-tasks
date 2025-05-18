package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.AttendanceSummary;
import tasks.entity.User;

import java.util.List;
import java.util.Optional;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, Long> {

    Optional<AttendanceSummary> findByUserAndYearAndMonth(User user, int year, int month);

    List<AttendanceSummary> findByYearAndMonth(int year, int month);
}
