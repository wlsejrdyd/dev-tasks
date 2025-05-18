package tasks.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AttendanceSummaryRow {
    private String name;
    private LocalDate joinDate;
    private LocalDate leaveDate;
    private double annualGiven;
    private double annualUsed;
    private double annualRemain;
    private double dayoffGiven;
    private double dayoffUsed;
    private double dayoffRemain;
    private double totalGiven;
    private double totalUsed;
    private double totalRemain;
}
