package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import tasks.entity.DutyRecord;

import java.time.LocalDate;

public interface DutyRecordRepository extends JpaRepository<DutyRecord, Long> {

    @Modifying
    @Query("DELETE FROM DutyRecord dr WHERE YEAR(dr.date) = :year AND MONTH(dr.date) = :month")
    void deleteByYearAndMonth(int year, int month);
}
