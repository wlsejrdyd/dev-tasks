package tasks.dto;

import lombok.Data;

@Data
public class AttendanceStatusResponse {
    private Long userId;
    private String userName;
    private String joinDate;
    private String leaveDate;
    private Double annualGranted;
    private Double annualUsed;
    private Double annualRemain;
    private Double compensatoryGranted;
    private Double compensatoryUsed;
    private Double compensatoryRemain;

    private Double totalGranted;
    private Double totalUsed;
    private Double totalRemain;
}
