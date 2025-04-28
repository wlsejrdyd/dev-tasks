package tasks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FindController {

    @GetMapping("/find-id")
    public String findIdPage(Model model) {
        return "find-id";
    }

    @GetMapping("/find-password")
    public String findPasswordPage(Model model) {
        return "find-password";
    }
}

