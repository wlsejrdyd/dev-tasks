package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tasks.entity.Notice;
import tasks.repository.NoticeRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeRepository noticeRepository;

    @GetMapping("/api/notices")
    public List<Notice> getNotices(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5); // 페이지 당 5개씩
        return noticeRepository.findAllByOrderByPinnedDescCreatedAtDesc(pageable).getContent();
    }
}
