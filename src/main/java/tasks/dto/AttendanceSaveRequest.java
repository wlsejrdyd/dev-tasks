package tasks.dto;

import lombok.Data;

@Data
public class AttendanceSaveRequest {
    private String name;
    private String joinDate;
    private String leaveDate;
    private double annualGiven;
    private double annualUsed;
    private double dayoffGiven;
    private double dayoffUsed;
}
