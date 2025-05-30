package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasks.dto.ScheduleRequest;
import tasks.entity.Schedule;
import tasks.repository.ScheduleRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public void save(ScheduleRequest request) {
        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .startDate(LocalDateTime.parse(request.getStartDate()))
                .endDate(LocalDateTime.parse(request.getEndDate()))
                .attendees(request.getAttendees())
                .time(request.getTime())
                .build();
        scheduleRepository.save(schedule);
    }

    public List<Schedule> findByYearAndMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);
        return scheduleRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(end, start);
    }

    @Transactional
    public void update(Long id, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        schedule.setTitle(request.getTitle());
        schedule.setContent(request.getContent());
        schedule.setStartDate(LocalDateTime.parse(request.getStartDate()));
        schedule.setEndDate(LocalDateTime.parse(request.getEndDate()));
        schedule.setAttendees(request.getAttendees());
        schedule.setTime(request.getTime());
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }
}
