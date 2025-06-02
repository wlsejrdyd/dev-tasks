package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.Poll;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}
