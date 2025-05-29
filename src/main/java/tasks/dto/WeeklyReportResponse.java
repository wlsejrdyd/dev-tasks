package tasks.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyReportResponse {
    private String date;
    private String category;
    private String title;
    private String content;
}
