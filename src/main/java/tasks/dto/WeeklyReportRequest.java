package tasks.dto;

import lombok.Data;

@Data
public class WeeklyReportRequest {
    private int year;
    private int month;
    private int week;
    private String date;
    private Long categoryId;
    private String title;
    private String content;
}
