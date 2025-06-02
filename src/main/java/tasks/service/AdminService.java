package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.dto.CategorySaveRequest;
import tasks.dto.NoticeRequest;
import tasks.dto.PollRequest;
import tasks.entity.Category;
import tasks.entity.Notice;
import tasks.repository.CategoryRepository;
import tasks.repository.NoticeRepository;
import tasks.service.PollService;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CategoryRepository categoryRepository;
    private final NoticeRepository noticeRepository;
    private final PollService pollService;

    public void saveCategory(CategorySaveRequest request) {
        categoryRepository.save(new Category(null, request.getName()));
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
