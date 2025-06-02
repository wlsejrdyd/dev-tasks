package tasks.dto;

import lombok.Data;

@Data
public class NoticeSaveRequest {
    private String title;
    private String content;
    private boolean pinned;
}
