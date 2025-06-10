package tasks.dto;

import lombok.Data;

@Data
public class WeeklyReportRequest {
    private String date;
    private Long categoryId;
    private String title;
    private String content;
}
