package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tasks.dto.AttendanceRecordRequest;
import tasks.dto.AttendanceRecordResponse;
import tasks.dto.AttendanceStatusResponse;
import tasks.entity.User;
import tasks.service.AttendanceService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceApiController {

    private final AttendanceService attendanceService;

    @PostMapping("/record")
    public void saveRecord(@RequestBody AttendanceRecordRequest request) {
        attendanceService.saveRecord(request);
    }

    @GetMapping("/records/{userId}")
    public List<AttendanceRecordResponse> getUserRecords(@PathVariable Long userId) {
        return attendanceService.getRecordsByUser(userId);
    }

    @GetMapping("/status")
    public List<AttendanceStatusResponse> getAllStatus() {
        return attendanceService.getAllStatus();
    }

    @DeleteMapping("/record/{id}")
    public void deleteRecord(@PathVariable Long id) {
        attendanceService.deleteRecord(id);
    }

    @GetMapping("/export")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=attendance.xlsx");
        attendanceService.exportExcel(response.getOutputStream());
    }

    @GetMapping("/users")
    public List<User> getAvailableUsers() {
        return attendanceService.getNonGuestUsers();
    }
}

