package tasks.dto;

import lombok.Data;

import java.util.List;

@Data
public class DutySaveRequest {
    private int year;
    private int month;
    private List<Row> rows;

    @Data
    public static class Row {
        private String time;         // 시간 구분 텍스트 (예: 주간 근무, 야간 근무)
        private List<String> data;   // 일 ~ 토 7칸 데이터, 각 칸에 복수 이름 포함 가능
    }
}
