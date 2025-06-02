package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.dto.CategorySaveRequest;
import tasks.dto.NoticeRequest;
import tasks.dto.PollRequest;
import tasks.entity.Notice;
import tasks.entity.WeeklyCategory;
import tasks.repository.NoticeRepository;
import tasks.repository.WeeklyCategoryRepository;
import tasks.service.PollService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final WeeklyCategoryRepository weeklyCategoryRepository;
    private final NoticeRepository noticeRepository;
    private final PollService pollService;

    public void saveWeeklyCategory(CategorySaveRequest request) {
        weeklyCategoryRepository.save(new WeeklyCategory(
            null,
            request.getName(),
            0,
            LocalDateTime.now(),
            LocalDateTime.now()
        ));
    }

    public void deleteWeeklyCategory(Long id) {
        weeklyCategoryRepository.deleteById(id);
    }

    public Notice saveNotice(NoticeRequest request) {
        return noticeRepository.save(Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .pinned(request.isPinned())
                .build());
    }

    public void savePoll(PollRequest request) {
        pollService.createPoll(request.getQuestion(), request.getOptions());
    }
}
