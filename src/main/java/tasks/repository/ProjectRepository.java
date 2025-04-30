package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.model.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findTop5ByOrderByStartDateDesc();
}
