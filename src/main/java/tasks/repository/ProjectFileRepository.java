package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.ProjectFile;

import java.util.List;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
    List<ProjectFile> findByProjectId(Long projectId);
}
