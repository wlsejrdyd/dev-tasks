package tasks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InternalServicePageController {

    @GetMapping("/services")
    public String showServicePage() {
        return "internal-service"; // internal-service.html
    }
}
