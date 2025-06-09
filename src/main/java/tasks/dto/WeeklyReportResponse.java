package tasks.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyReportResponse {
    private String date;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String content;
}
