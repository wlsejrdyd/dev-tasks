package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.dto.SchedulePreviewResponse;
import tasks.entity.Schedule;
import tasks.repository.InternalServiceRepository;
import tasks.repository.ProjectRepository;
import tasks.repository.ScheduleRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final ScheduleRepository scheduleRepository;
    private final InternalServiceRepository internalServiceRepository;
    private final DnsRecordService dnsRecordService;

    public long getProjectCount() {
        return projectRepository.count();
    }

    public long getProjectCountByStatus(String status) {
        return projectRepository.countByStatus(status);
    }

    public int getIpCount() {
        return 0;
    }

    public long getDnsCount() {
        return dnsRecordService.countUniqueMaindomains();
    }

    public int getServiceCount() {
        return 0;
    }

    public long getInternalServiceTotalCount() {
        return internalServiceRepository.count();
    }

    // 🟢 다가오는 일정 조회
    public List<SchedulePreviewResponse> getUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        List<Schedule> schedules = scheduleRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                now.plusDays(30), now.minusDays(1)
        );

        return schedules.stream()
                .map(s -> new SchedulePreviewResponse(
                    s.getStartDate(),
                    s.getEndDate(),
                    "작성자", // 추후 User 엔티티 연동 시 대체
                    s.getTitle(),
                    s.getAttendees()
                ))
                .sorted(Comparator.comparing(SchedulePreviewResponse::getEndDate))
                .toList();
    }
}
