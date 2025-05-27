package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.DutyCell;

import java.util.List;

public interface DutyCellRepository extends JpaRepository<DutyCell, Long> {
    List<DutyCell> findByYearAndMonth(int year, int month);
    void deleteByYearAndMonth(int year, int month);
}

