package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tasks.entity.Notice;
import tasks.repository.NoticeRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeRepository noticeRepository;

    @GetMapping("/api/notices")
    public List<Notice> getRecentNotices() {
        return noticeRepository.findTop5ByOrderByPinnedDescCreatedAtDesc();
    }
}
