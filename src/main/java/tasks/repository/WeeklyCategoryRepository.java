package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasks.entity.WeeklyCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyCategoryRepository extends JpaRepository<WeeklyCategory, Long> {
    List<WeeklyCategory> findAllByOrderBySortOrderAsc();

    Optional<WeeklyCategory> findByName(String name); // 🔧 추가
}
