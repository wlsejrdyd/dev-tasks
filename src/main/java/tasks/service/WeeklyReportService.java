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
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
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
        // 기존 데이터 삭제
        List<WeeklyReport> existing = reportRepository.findByYearAndMonthAndWeek(year, month, week);
        reportRepository.deleteAll(existing);

        // 새 데이터 저장
        List<WeeklyReport> newReports = requests.stream()
                .map(req -> {
                    LocalDate date = LocalDate.parse(req.getDate());
                    int y = date.getYear();
                    int m = date.getMonthValue();
                    int w = calculateWeekOfMonth(date);

                    WeeklyCategory cat = categoryRepository.findById(req.getCategoryId()).orElse(null);
                    return WeeklyReport.builder()
                            .year(y)
                            .month(m)
                            .week(w)
                            .date(date)
                            .category(cat != null ? cat.getName() : null)
                            .title(req.getTitle())
                            .content(req.getContent())
                            .build();
                })
                .collect(Collectors.toList());

        reportRepository.saveAll(newReports);
    }

    private int calculateWeekOfMonth(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        return date.get(weekFields.weekOfMonth());
    }
}
