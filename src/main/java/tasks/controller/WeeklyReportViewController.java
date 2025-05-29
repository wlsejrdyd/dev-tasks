package tasks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeeklyReportViewController {
    @GetMapping("/weekly")
    public String weeklyPage() {
        return "weekly"; // templates/weekly.html
    }
}
