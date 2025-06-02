package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.Notice;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findTop5ByOrderByPinnedDescCreatedAtDesc();
}
