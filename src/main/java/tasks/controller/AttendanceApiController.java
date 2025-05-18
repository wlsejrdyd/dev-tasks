package tasks.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasks.dto.AttendanceSaveRequest;
import tasks.entity.AttendanceSummary;
import tasks.entity.User;
import tasks.entity.User.Role;
import tasks.repository.AttendanceSummaryRepository;
import tasks.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceApiController {

    private final UserRepository userRepository;
    private final AttendanceSummaryRepository summaryRepository;

    @PostMapping("/save-summary")
    @Transactional
    public ResponseEntity<String> saveSummary(@RequestBody List<AttendanceSaveRequest> requestList) {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        // 이름 목록 수집
        Set<String> incomingNames = requestList.stream()
                .map(AttendanceSaveRequest::getName)
                .collect(Collectors.toSet());

        // 기존 저장된 사용자 전체
        List<AttendanceSummary> existingSummaries = summaryRepository.findByYearAndMonth(year, month);

        // 삭제 대상: 요청에 없는 이름들
        for (AttendanceSummary existing : existingSummaries) {
            String savedName = existing.getUser().getName();
            if (!incomingNames.contains(savedName)) {
                summaryRepository.delete(existing);
            }
        }

        // 저장/수정 처리
        for (AttendanceSaveRequest req : requestList) {
            if (req.getName() == null || req.getName().isBlank()) continue;

            // 유저 없으면 생성
            User user = userRepository.findByName(req.getName())
                    .orElseGet(() -> userRepository.save(User.builder()
                            .name(req.getName())
                            .username(req.getName())
                            .email(req.getName() + "@auto.local")
                            .password("!")
                            .role(Role.USER)  // ✅ 내부 enum 사용
                            .createdAt(LocalDateTime.now())
                            .build()));

            AttendanceSummary summary = summaryRepository
                    .findByUserAndYearAndMonth(user, year, month)
                    .orElseGet(() -> AttendanceSummary.builder()
                            .user(user)
                            .year(year)
                            .month(month)
                            .createdAt(LocalDateTime.now())
                            .build());

            summary.setAnnualGiven(req.getAnnualGiven());
            summary.setAnnualUsed(req.getAnnualUsed());
            summary.setDayoffGiven(req.getDayoffGiven());
            summary.setDayoffUsed(req.getDayoffUsed());

            summaryRepository.save(summary);
        }

        return ResponseEntity.ok("✅ 저장 완료");
    }
}
