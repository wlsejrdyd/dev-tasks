package tasks.dto;

import lombok.Data;

@Data
public class NoticeRequest {
    private String title;
    private String content;
    private boolean pinned;
}
