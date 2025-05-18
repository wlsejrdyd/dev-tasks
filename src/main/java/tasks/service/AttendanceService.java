package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.dto.AttendanceSummaryRow;
import tasks.entity.AttendanceRecord;
import tasks.entity.AttendanceSummary;
import tasks.entity.User;
import tasks.repository.AttendanceRecordRepository;
import tasks.repository.AttendanceSummaryRepository;
import tasks.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final UserRepository userRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;

    public List<AttendanceRecord> getAllRecords() {
        return attendanceRecordRepository.findAll();
    }

    // ✅ 저장된 요약값 조회
    public List<AttendanceSummaryRow> getAttendanceSummaryRowsFromDB(int year, int month) {
        List<AttendanceSummary> savedList = attendanceSummaryRepository.findByYearAndMonth(year, month);

        return savedList.stream().map(summary -> AttendanceSummaryRow.builder()
                .name(summary.getUser().getName())
                .joinDate(summary.getUser().getCreatedAt().toLocalDate())  // 향후 컬럼 분리 가능
                .leaveDate(null)  // TODO: leaveDate 확장 시 반영
                .annualGiven(summary.getAnnualGiven())
                .annualUsed(summary.getAnnualUsed())
                .annualRemain(summary.getAnnualGiven() + summary.getAnnualUsed())
                .dayoffGiven(summary.getDayoffGiven())
                .dayoffUsed(summary.getDayoffUsed())
                .dayoffRemain(summary.getDayoffGiven() + summary.getDayoffUsed())
                .totalGiven(summary.getAnnualGiven() + summary.getDayoffGiven())
                .totalUsed(summary.getAnnualUsed() + summary.getDayoffUsed())
                .totalRemain(summary.getAnnualGiven() + summary.getAnnualUsed() +
                        summary.getDayoffGiven() + summary.getDayoffUsed())
                .build()
        ).collect(Collectors.toList());
    }

    // ✅ 기존 계산 기반 요약
    public List<AttendanceSummaryRow> getAttendanceSummaryRows() {
        List<User> users = userRepository.findAll();
        List<AttendanceRecord> records = attendanceRecordRepository.findAll();

        Map<Long, List<AttendanceRecord>> recordsByUser = records.stream()
                .collect(Collectors.groupingBy(r -> r.getUser().getId()));

        List<AttendanceSummaryRow> result = new ArrayList<>();

        for (User user : users) {
            List<AttendanceRecord> userRecords = recordsByUser.getOrDefault(user.getId(), new ArrayList<>());

            double annualGiven = 17.0; // 설정값으로 대체 가능
            double annualUsed = sumDays(userRecords, "연차 사용");
            double annualRemain = annualGiven + annualUsed;

            double dayoffGiven = sumDays(userRecords, "대휴 부여");
            double dayoffUsed = sumDays(userRecords, "대휴 사용");
            double dayoffRemain = dayoffGiven + dayoffUsed;

            double totalGiven = annualGiven + dayoffGiven;
            double totalUsed = annualUsed + dayoffUsed;
            double totalRemain = annualRemain + dayoffRemain;

            AttendanceSummaryRow row = AttendanceSummaryRow.builder()
                    .name(user.getName())
                    .joinDate(user.getCreatedAt().toLocalDate())
                    .leaveDate(null)
                    .annualGiven(annualGiven)
                    .annualUsed(annualUsed)
                    .annualRemain(annualRemain)
                    .dayoffGiven(dayoffGiven)
                    .dayoffUsed(dayoffUsed)
                    .dayoffRemain(dayoffRemain)
                    .totalGiven(totalGiven)
                    .totalUsed(totalUsed)
                    .totalRemain(totalRemain)
                    .build();

            result.add(row);
        }

        return result;
    }

    private double sumDays(List<AttendanceRecord> records, String type) {
        return records.stream()
                .filter(r -> type.equals(r.getType()))
                .mapToDouble(AttendanceRecord::getDays)
                .sum();
    }
}
