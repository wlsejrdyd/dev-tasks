package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.PollOption;

public interface PollOptionRepository extends JpaRepository<PollOption, Long> {
}
