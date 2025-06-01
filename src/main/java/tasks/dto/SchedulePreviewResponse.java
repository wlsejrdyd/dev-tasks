package tasks.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePreviewResponse {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String createdBy;
    private String title;
    private String attendees;
}
