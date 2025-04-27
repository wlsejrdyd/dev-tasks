package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}

