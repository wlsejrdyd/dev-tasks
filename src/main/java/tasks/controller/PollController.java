package tasks.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;
import tasks.entity.Poll;
import tasks.entity.PollOption;
import tasks.entity.PollVote;
import tasks.repository.PollOptionRepository;
import tasks.repository.PollRepository;
import tasks.repository.PollVoteRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final PollVoteRepository voteRepository;

    @GetMapping
    public Poll getPoll() {
        return pollRepository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElse(null);
    }

    @PostMapping("/vote")
    public String vote(@RequestBody VoteRequest vote) {
        Optional<PollVote> existing = voteRepository.findByPollIdAndUserId(vote.getPollId(), vote.getUserId());
        if (existing.isPresent()) {
            return "이미 투표했습니다.";
        }

        Poll poll = pollRepository.findById(vote.getPollId()).orElseThrow();
        PollOption option = optionRepository.findById(vote.getOptionId()).orElseThrow();

        option.setVoteCount(option.getVoteCount() + 1);
        optionRepository.save(option);

        voteRepository.save(PollVote.builder()
                .poll(poll)
                .userId(vote.getUserId())
                .selectedOption(option)
                .build());

        return "ok";
    }

    @Getter
    @Setter
    public static class VoteRequest {
        private Long pollId;
        private Long optionId;
        private Long userId = 1L; // TODO: 실제 로그인 사용자와 연결
    }
}
