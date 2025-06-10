package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tasks.dto.WeeklyReportRequest;
import tasks.dto.WeeklyReportResponse;
import tasks.dto.WeeklyReportSaveRequest;
import tasks.service.WeeklyReportService;

import java.util.List;

@RestController
@RequestMapping("/api/weekly")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final WeeklyReportService weeklyReportService;

    @GetMapping("/reports")
    public List<WeeklyReportResponse> getReports(@RequestParam int year,
                                                 @RequestParam int month,
                                                 @RequestParam int week) {
        return weeklyReportService.getWeeklyReports(year, month, week);
    }

    @PostMapping("/reports")
    public void saveWeeklyReports(@RequestBody WeeklyReportSaveRequest request) {
        weeklyReportService.saveWeeklyReports(
            request.getYear(),
            request.getMonth(),
            request.getWeek(),
            request.getReports()
        );
    }
}

