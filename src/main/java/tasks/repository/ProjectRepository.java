package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tasks.entity.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.createdBy")
    List<Project> findAllWithCreatedBy();

    List<Project> findByStatus(String status);

    List<Project> findByNameContainingIgnoreCaseAndStatus(String name, String status);

    long countByStatus(String status); // ✅ 상태별 프로젝트 수 카운트
}
