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
                    res.setAnnualGranted(status.getAnnualGranted().doubleValue());
                    res.setAnnualUsed(status.getAnnualUsed().doubleValue());
                    res.setAnnualRemain(status.getAnnualGranted().subtract(status.getAnnualUsed()).doubleValue());
                    res.setCompensatoryGranted(status.getCompensatoryGranted().doubleValue());
                    res.setCompensatoryUsed(status.getCompensatoryUsed().doubleValue());
                    res.setCompensatoryRemain(status.getCompensatoryGranted().subtract(status.getCompensatoryUsed()).doubleValue());
                    return res;
                }).collect(Collectors.toList());
    }

    @Transactional
    public void updateStatusFromRecords(User user) {
        List<AttendanceRecord> records = recordRepository.findByUser(user);

        BigDecimal annualUsed = records.stream()
                .filter(r -> r.getType() == AttendanceType.연차)
                .map(AttendanceRecord::getDays)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal compUsed = records.stream()
                .filter(r -> r.getType() == AttendanceType.대휴)
                .map(AttendanceRecord::getDays)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AttendanceStatus status = statusRepository.findByUser(user)
                .orElse(AttendanceStatus.builder()
                        .user(user)
                        .joinDate(LocalDate.now())
                        .annualGranted(BigDecimal.ZERO)
                        .compensatoryGranted(BigDecimal.ZERO)
                        .build());

        status.setAnnualUsed(annualUsed);
        status.setCompensatoryUsed(compUsed);

        statusRepository.save(status);
    }

    @Transactional
    public void deleteRecord(Long id) {
        AttendanceRecord record = recordRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록"));
    
        User user = record.getUser(); // 삭제 대상의 사용자 참조
        recordRepository.deleteById(id);
        updateStatusFromRecords(user); // 삭제 후 요약 갱신
    }
}
