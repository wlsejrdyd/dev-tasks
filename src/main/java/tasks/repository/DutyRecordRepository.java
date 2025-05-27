package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.DutyRecord;

public interface DutyRecordRepository extends JpaRepository<DutyRecord, Long> {
}
