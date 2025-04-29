package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
