package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tasks.service.DashboardService;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("username", dashboardService.getCurrentUsername());
        model.addAttribute("projects", dashboardService.getProjectOverview());
        model.addAttribute("ips", dashboardService.getIpStatus());
        model.addAttribute("domains", dashboardService.getDnsDomains());
        model.addAttribute("attendances", dashboardService.getAttendanceStatus());
        return "dashboard";
    }
}
