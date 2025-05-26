package tasks.dto;

import lombok.Data;
import tasks.entity.enums.AttendanceType;

@Data
public class AttendanceRecordRequest {
    private Long userId;
    private AttendanceType type;
    private String startDate; // "2025-05-01"
    private String endDate;
    private Double days;
    private String reason;
}
