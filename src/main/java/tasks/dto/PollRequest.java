package tasks.dto;

import lombok.Data;

import java.util.List;

@Data
public class PollRequest {
    private String question;
    private List<String> options;
}
