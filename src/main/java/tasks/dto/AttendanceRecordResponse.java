package tasks.dto;

import lombok.Data;
import tasks.entity.enums.AttendanceType;

@Data
public class AttendanceRecordResponse {
    private Long id;
    private Long userId; // ✅ 추가됨
    private String userName;
    private AttendanceType type;
    private String startDate;
    private String endDate;
    private Double days;
    private String reason;
}
