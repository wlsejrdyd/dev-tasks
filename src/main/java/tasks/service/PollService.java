package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.Poll;
import tasks.entity.PollOption;
import tasks.repository.PollOptionRepository;
import tasks.repository.PollRepository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;

    @Transactional
    public void createPoll(String question, List<String> options) {
        Poll poll = new Poll();
        poll.setQuestion(question);
        poll.setActive(true);
        pollRepository.save(poll);

        List<PollOption> pollOptions = options.stream()
                .map(opt -> PollOption.builder()
                        .optionText(opt)
                        .poll(poll)
                        .build())
                .collect(Collectors.toList());

        pollOptionRepository.saveAll(pollOptions);
    }
}
