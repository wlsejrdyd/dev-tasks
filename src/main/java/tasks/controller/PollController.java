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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final PollVoteRepository voteRepository;

    @GetMapping
    public Map<String, Object> getPollWithVoteStatus(@RequestParam(required = false, defaultValue = "1") Long userId) {
        Optional<Poll> optionalPoll = pollRepository.findFirstByIsActiveTrueOrderByCreatedAtDesc();

        if (optionalPoll.isEmpty()) return Collections.emptyMap();

        Poll poll = optionalPoll.get();
        boolean voted = voteRepository.findByPollIdAndUserId(poll.getId(), userId).isPresent();

        List<Map<String, Object>> options = poll.getOptions().stream().map(opt -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", opt.getId());
            map.put("optionText", opt.getOptionText());
            map.put("voteCount", opt.getVoteCount());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("id", poll.getId());
        response.put("question", poll.getQuestion());
        response.put("voted", voted);
        response.put("options", options);
        return response;
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
