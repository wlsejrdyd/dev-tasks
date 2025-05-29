package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasks.dto.WeeklyReportRequest;
import tasks.dto.WeeklyReportResponse;
import tasks.entity.WeeklyReport;
import tasks.repository.WeeklyReportRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final WeeklyReportRepository reportRepository;

    public List<WeeklyReportResponse> getWeeklyReports(int year, int month, int week) {
        return reportRepository.findByYearAndMonthAndWeek(year, month, week)
                .stream()
                .map(report -> WeeklyReportResponse.builder()
                        .date(report.getDate() != null ? report.getDate().toString() : "")
                        .category(report.getCategory())
                        .title(report.getTitle())
                        .content(report.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveWeeklyReports(int year, int month, int week, List<WeeklyReportRequest> requests) {
        // 기존 데이터 삭제
        List<WeeklyReport> existing = reportRepository.findByYearAndMonthAndWeek(year, month, week);
        reportRepository.deleteAll(existing);

        // 새 데이터 저장
        List<WeeklyReport> newReports = requests.stream()
                .map(req -> WeeklyReport.builder()
                        .year(year)
                        .month(month)
                        .week(week)
                        .date(req.getDate() != null && !req.getDate().isBlank() ? LocalDate.parse(req.getDate()) : null)
                        .category(req.getCategory())
                        .title(req.getTitle())
                        .content(req.getContent())
                        .build())
                .collect(Collectors.toList());

        reportRepository.saveAll(newReports);
    }
}
