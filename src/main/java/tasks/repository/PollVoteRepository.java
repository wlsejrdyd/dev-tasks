package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.PollVote;

import java.util.Optional;

public interface PollVoteRepository extends JpaRepository<PollVote, Long> {
    Optional<PollVote> findByPollIdAndUserId(Long pollId, Long userId);
}
