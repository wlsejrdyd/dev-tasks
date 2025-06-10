package tasks.dto;

import lombok.Data;
import java.util.List;

@Data
public class WeeklyReportSaveRequest {
    private int year;
    private int month;
    private int week;
    private List<WeeklyReportRequest> reports;
}
