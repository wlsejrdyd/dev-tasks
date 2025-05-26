package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tasks.dto.AttendanceRecordRequest;
import tasks.dto.AttendanceRecordResponse;
import tasks.dto.AttendanceStatusResponse;
import tasks.service.AttendanceService;

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
}
