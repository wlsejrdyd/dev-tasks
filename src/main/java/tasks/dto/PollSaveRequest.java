package tasks.dto;

import lombok.Data;

import java.util.List;

@Data
public class PollSaveRequest {
    private String question;
    private List<String> options;
}
