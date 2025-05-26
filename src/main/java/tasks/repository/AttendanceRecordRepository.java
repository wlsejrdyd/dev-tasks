package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasks.entity.AttendanceRecord;
import tasks.entity.User;
import tasks.entity.enums.AttendanceType;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByUser(User user);

    List<AttendanceRecord> findByUserAndType(User user, AttendanceType type);

    List<AttendanceRecord> findByUserAndStartDateBetween(User user, LocalDate start, LocalDate end);
}
