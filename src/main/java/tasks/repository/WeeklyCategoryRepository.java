package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasks.entity.WeeklyCategory;

import java.util.List;

@Repository
public interface WeeklyCategoryRepository extends JpaRepository<WeeklyCategory, Long> {
    List<WeeklyCategory> findAllByOrderBySortOrderAsc();
}
