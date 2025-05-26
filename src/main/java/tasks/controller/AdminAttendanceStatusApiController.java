package tasks.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tasks.entity.AttendanceStatus;
import tasks.entity.User;
import tasks.repository.AttendanceStatusRepository;
import tasks.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/attendance-status")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAttendanceStatusApiController {

    private final AttendanceStatusRepository statusRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> updateStatus(@RequestBody UpdateRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("사용자 없음");

        AttendanceStatus status = statusRepository.findByUser(user)
                .orElse(AttendanceStatus.builder()
                        .user(user)
                        .joinDate(LocalDate.now())
                        .annualGranted(BigDecimal.ZERO)
                        .compensatoryGranted(BigDecimal.ZERO)
                        .build());

        if (request.getJoinDate() != null)
            status.setJoinDate(request.getJoinDate());
        status.setLeaveDate(request.getLeaveDate());

        if (request.getAnnualGranted() != null)
            status.setAnnualGranted(BigDecimal.valueOf(request.getAnnualGranted()));

        statusRepository.save(status);
        return ResponseEntity.ok("저장 완료");
    }

    @Data
    public static class UpdateRequest {
        private Long userId;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate joinDate;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate leaveDate;
        private Double annualGranted;
    }
}
