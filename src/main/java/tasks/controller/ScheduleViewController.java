package tasks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScheduleViewController {

    @GetMapping("/schedule")
    public String showSchedulePage() {
        return "schedule";
    }
}
