package tasks.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DutyStatResponse {
    private String name;
    private int day;
    private int night;

    // ✅ 추가 필드
    private int weekdayDay;
    private int weekdayNight;
    private int weekendDay;
    private int weekendNight;
    private int holidayDay;
    private int holidayNight;
}
