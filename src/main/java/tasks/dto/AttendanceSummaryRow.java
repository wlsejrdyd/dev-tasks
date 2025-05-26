package tasks.dto;

import lombok.Data;

@Data
public class AttendanceSummaryRow {
    private Long userId;
    private String name;
    private String joinDate;
    private String leaveDate;
    private double annualGranted;
    private double annualUsed;
    private double annualRemain;
    private double compensatoryGranted;
    private double compensatoryUsed;
    private double compensatoryRemain;
}
