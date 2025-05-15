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
        model.addAttribute("projectCount", dashboardService.getProjectCount());
        model.addAttribute("ipCount", dashboardService.getIpCount());
        model.addAttribute("dnsCount", dashboardService.getDnsCount());
        model.addAttribute("serviceCount", dashboardService.getServiceCount());
        return "dashboard";
    }
}
