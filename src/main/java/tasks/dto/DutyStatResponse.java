package tasks.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DutyStatResponse {
    private String name;
    private int day;
    private int night;
}
