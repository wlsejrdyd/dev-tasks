package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasks.dto.AttendanceRecordRequest;
import tasks.dto.AttendanceRecordResponse;
import tasks.dto.AttendanceStatusResponse;
import tasks.entity.AttendanceRecord;
import tasks.entity.AttendanceStatus;
import tasks.entity.User;
import tasks.entity.enums.AttendanceType;
import tasks.repository.AttendanceRecordRepository;
import tasks.repository.AttendanceStatusRepository;
import tasks.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRecordRepository recordRepository;
    private final AttendanceStatusRepository statusRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveRecord(AttendanceRecordRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        AttendanceRecord record = AttendanceRecord.builder()
                .user(user)
                .type(request.getType())
                .startDate(LocalDate.parse(request.getStartDate()))
                .endDate(LocalDate.parse(request.getEndDate()))
                .days(BigDecimal.valueOf(Math.abs(request.getDays())))
                .reason(request.getReason())
                .build();

        recordRepository.save(record);
        updateStatusFromRecords(user);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecordResponse> getRecordsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return recordRepository.findByUser(user).stream()
                .map(r -> {
                    AttendanceRecordResponse res = new AttendanceRecordResponse();
                    res.setId(r.getId());
                    res.setUserName(user.getName());
                    res.setType(r.getType());
                    res.setStartDate(r.getStartDate().toString());
                    res.setEndDate(r.getEndDate().toString());
                    res.setDays(r.getDays().doubleValue());
                    res.setReason(r.getReason());
                    return res;
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceStatusResponse> getAllStatus() {
        return statusRepository.findAll().stream()
                .map(status -> {
                    AttendanceStatusResponse res = new AttendanceStatusResponse();
                    res.setUserId(status.getUser().getId());
                    res.setUserName(status.getUser().getName());
                    res.setJoinDate(status.getJoinDate().toString());
                    res.setLeaveDate(status.getLeaveDate() != null ? status.getLeaveDate().toString() : null);

                    BigDecimal annualGranted = safe(status.getAnnualGranted());
                    BigDecimal annualUsed = safe(status.getAnnualUsed());
                    BigDecimal compGranted = safe(status.getCompensatoryGranted());
                    BigDecimal compUsed = safe(status.getCompensatoryUsed());

                    res.setAnnualGranted(annualGranted.doubleValue());
                    res.setAnnualUsed(annualUsed.doubleValue());
                    res.setAnnualRemain(annualGranted.subtract(annualUsed).doubleValue());
                    res.setCompensatoryGranted(compGranted.doubleValue());
                    res.setCompensatoryUsed(compUsed.doubleValue());
                    res.setCompensatoryRemain(compGranted.subtract(compUsed).doubleValue());

                    return res;
                }).collect(Collectors.toList());
    }

    @Transactional
    public void updateStatusFromRecords(User user) {
        List<AttendanceRecord> records = recordRepository.findByUser(user);

        BigDecimal annualUsed = BigDecimal.ZERO;
        BigDecimal compUsed = BigDecimal.ZERO;
        BigDecimal compGranted = BigDecimal.ZERO;

        for (AttendanceRecord record : records) {
            BigDecimal days = safe(record.getDays());
            switch (record.getType()) {
                case 연차사용 -> annualUsed = annualUsed.add(days);
                case 대휴사용 -> compUsed = compUsed.add(days);
                case 대휴부여 -> compGranted = compGranted.add(days);
                case 기타 -> {} // 무시
            }
        }

        AttendanceStatus status = statusRepository.findByUser(user)
                .orElseGet(() -> AttendanceStatus.builder()
                        .user(user)
                        .joinDate(LocalDate.now())
                        .annualGranted(BigDecimal.ZERO)
                        .compensatoryGranted(BigDecimal.ZERO)
                        .build());

        if (status.getAnnualGranted() == null) status.setAnnualGranted(BigDecimal.ZERO);
        if (status.getCompensatoryGranted() == null) status.setCompensatoryGranted(BigDecimal.ZERO);

        status.setAnnualUsed(annualUsed);
        status.setCompensatoryUsed(compUsed);
        status.setCompensatoryGranted(compGranted);

        statusRepository.save(status);
    }

    @Transactional
    public void deleteRecord(Long id) {
        AttendanceRecord record = recordRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록"));

        User user = record.getUser();
        recordRepository.deleteById(id);
        updateStatusFromRecords(user);
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
