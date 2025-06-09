package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasks.dto.WeeklyReportRequest;
import tasks.dto.WeeklyReportResponse;
import tasks.entity.WeeklyCategory;
import tasks.entity.WeeklyReport;
import tasks.repository.WeeklyCategoryRepository;
import tasks.repository.WeeklyReportRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final WeeklyReportRepository reportRepository;
    private final WeeklyCategoryRepository categoryRepository;

    public List<WeeklyReportResponse> getWeeklyReports(int year, int month, int week) {
        return reportRepository.findByYearAndMonthAndWeek(year, month, week)
                .stream()
                .map(report -> {
                    WeeklyCategory category = categoryRepository.findByName(report.getCategory()).orElse(null);
                    return WeeklyReportResponse.builder()
                            .date(report.getDate() != null ? report.getDate().toString() : "")
                            .categoryId(category != null ? category.getId() : null)
                            .categoryName(report.getCategory())
                            .title(report.getTitle())
                            .content(report.getContent())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveWeeklyReports(int year, int month, int week, List<WeeklyReportRequest> requests) {
        List<WeeklyReport> existing = reportRepository.findByYearAndMonthAndWeek(year, month, week);
        reportRepository.deleteAll(existing);

        List<WeeklyReport> newReports = requests.stream()
                .map(req -> {
                    WeeklyCategory cat = categoryRepository.findById(req.getCategoryId()).orElse(null);
                    return WeeklyReport.builder()
                            .year(year)
                            .month(month)
                            .week(week)
                            .date(req.getDate() != null && !req.getDate().isBlank() ? LocalDate.parse(req.getDate()) : null)
                            .category(cat != null ? cat.getName() : null)
                            .title(req.getTitle())
                            .content(req.getContent())
                            .build();
                })
                .collect(Collectors.toList());

        reportRepository.saveAll(newReports);
    }
}
