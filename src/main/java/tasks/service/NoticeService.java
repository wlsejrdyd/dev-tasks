package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.Notice;
import tasks.repository.NoticeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    public Notice save(String title, String content, boolean pinned) {
        return noticeRepository.save(
            Notice.builder()
                .title(title)
                .content(content)
                .pinned(pinned)
                .createdAt(LocalDateTime.now())
                .build()
        );
    }
}
