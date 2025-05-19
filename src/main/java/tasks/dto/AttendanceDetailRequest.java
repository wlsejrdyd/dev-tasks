package tasks.dto;

import lombok.Data;

@Data
public class AttendanceDetailRequest {
    private String name;       // 사용자 실명
    private String type;       // 연차 사용, 대휴 부여, 대휴 사용, 기타
    private String startDate;  // yyyy-MM-dd
    private String endDate;    // yyyy-MM-dd
    private double days;       // + 또는 - 가능
    private String reason;     // 사유
}
