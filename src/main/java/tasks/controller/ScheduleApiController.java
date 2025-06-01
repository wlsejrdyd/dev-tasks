package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import tasks.entity.Schedule;
import tasks.repository.ScheduleRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleApiController {

    private final ScheduleRepository scheduleRepository;

    @GetMapping("/upcoming")
    public List<Schedule> getUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        return scheduleRepository.findByEndDateAfterOrderByEndDateAsc(now);
    }
}

