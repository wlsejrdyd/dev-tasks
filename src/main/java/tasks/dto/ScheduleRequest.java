package tasks.dto;

import lombok.Data;

@Data
public class ScheduleRequest {
    private String title;
    private String content;
    private String startDate;
    private String endDate;
    private String attendees;
    private String time;
}
