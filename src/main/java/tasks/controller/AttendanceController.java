package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tasks.repository.UserRepository;

@Controller
@RequiredArgsConstructor
public class AttendanceController {

    private final UserRepository userRepository;

    @GetMapping("/attendance")
    public String attendancePage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "attendance";
    }
}
