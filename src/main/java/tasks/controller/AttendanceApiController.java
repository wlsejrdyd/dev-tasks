package tasks.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasks.dto.AttendanceSaveRequest;
import tasks.dto.AttendanceDetailRequest;
import tasks.entity.AttendanceSummary;
import tasks.entity.AttendanceRecord;
import tasks.entity.User;
import tasks.entity.User.Role;
import tasks.repository.AttendanceSummaryRepository;
import tasks.repository.AttendanceRecordRepository;
import tasks.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceApiController {

    private final UserRepository userRepository;
    private final AttendanceSummaryRepository summaryRepository;
    private final AttendanceRecordRepository recordRepository;

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
                            .role(Role.USER)
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

    @PostMapping("/save-detail")
    @Transactional
    public ResponseEntity<String> saveDetail(@RequestBody List<AttendanceDetailRequest> requestList) {
        int saved = 0, skipped = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (AttendanceDetailRequest req : requestList) {
            try {
                if (req.getName() == null || req.getName().isBlank()) {
                    skipped++;
                    continue;
                }

                User user = userRepository.findByName(req.getName())
                        .orElseGet(() -> userRepository.save(User.builder()
                                .name(req.getName())
                                .username(req.getName())
                                .email(req.getName() + "@auto.local")
                                .password("!")
                                .role(Role.USER)
                                .createdAt(LocalDateTime.now())
                                .build()));

                AttendanceRecord record = AttendanceRecord.builder()
                        .user(user)
                        .type(req.getType())
                        .startDate(LocalDate.parse(req.getStartDate(), formatter))
                        .endDate(LocalDate.parse(req.getEndDate(), formatter))
                        .days((int) req.getDays()) // float 값이 아닌 정수로 저장됨
                        .reason(req.getReason())
                        .build();

                recordRepository.save(record);
                saved++;

            } catch (Exception e) {
                skipped++;
            }
        }

        return ResponseEntity.ok("상세 내역 저장 완료: 저장 " + saved + "건, 건너뜀 " + skipped + "건");
    }
}
