package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tasks.dto.WeeklyReportRequest;
import tasks.dto.WeeklyReportResponse;
import tasks.service.WeeklyReportService;

import java.util.List;

@RestController
@RequestMapping("/api/weekly")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final WeeklyReportService weeklyReportService;

    @GetMapping
    public List<WeeklyReportResponse> getWeeklyReports(@RequestParam int year,
                                                       @RequestParam int month,
                                                       @RequestParam int week) {
        return weeklyReportService.getWeeklyReports(year, month, week);
    }

    @PostMapping
    public void saveWeeklyReports(@RequestParam int year,
                                  @RequestParam int month,
                                  @RequestParam int week,
                                  @RequestBody List<WeeklyReportRequest> reports) {
        weeklyReportService.saveWeeklyReports(year, month, week, reports);
    }
}
