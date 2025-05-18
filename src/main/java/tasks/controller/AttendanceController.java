package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tasks.dto.AttendanceSummaryRow;
import tasks.entity.AttendanceRecord;
import tasks.service.AttendanceService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/attendance")
    public String viewAttendancePage(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") LocalDate month,
            Model model
    ) {
        if (month == null) {
            month = LocalDate.now().withDayOfMonth(1);
        }

        int year = month.getYear();
        int mon = month.getMonthValue();

        List<AttendanceRecord> allRecords = attendanceService.getAllRecords();

        // ✅ 저장된 값 있으면 우선 사용, 없으면 계산 방식 fallback
        List<AttendanceSummaryRow> summaryRows = attendanceService.getAttendanceSummaryRowsFromDB(year, mon);
        if (summaryRows.isEmpty()) {
            summaryRows = attendanceService.getAttendanceSummaryRows();
        }

        model.addAttribute("selectedMonth", month);
        model.addAttribute("records", allRecords);
        model.addAttribute("mainTableList", summaryRows);

        return "attendance";
    }
}
