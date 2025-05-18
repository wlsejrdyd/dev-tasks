package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tasks.service.DashboardService;
import tasks.service.DashboardIpStatService;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardIpStatService dashboardIpStatService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("projectCount", dashboardService.getProjectCount());
        model.addAttribute("ipCount", dashboardService.getIpCount());
        model.addAttribute("dnsCount", dashboardService.getDnsCount());
        model.addAttribute("serviceCount", dashboardService.getServiceCount());

        model.addAttribute("projectInProgress", dashboardService.getProjectCountByStatus("진행중"));
        model.addAttribute("projectCompleted", dashboardService.getProjectCountByStatus("완료"));
        model.addAttribute("projectPending", dashboardService.getProjectCountByStatus("보류"));

        model.addAttribute("ipRangeCount", dashboardIpStatService.getIpRangeCount());
        model.addAttribute("ipUpCount", dashboardIpStatService.getIpUpCount());
        model.addAttribute("ipDownCount", dashboardIpStatService.getIpDownCount());

        // ✅ 내부 서비스 수 개별 카드용
        model.addAttribute("internalServiceTotal", dashboardService.getInternalServiceTotalCount());

        return "dashboard";
    }
}
