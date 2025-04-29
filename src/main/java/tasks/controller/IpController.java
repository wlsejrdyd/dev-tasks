package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.model.IpAddress;
import tasks.service.IpService;

@Controller
@RequestMapping("/ips")
@RequiredArgsConstructor
public class IpController {

    private final IpService ipService;

    @GetMapping
    public String listIps(Model model) {
        model.addAttribute("ips", ipService.getAllIps());
        return "ips";
    }

    @GetMapping("/new")
    public String newIpForm(Model model) {
        model.addAttribute("ipAddress", new IpAddress());
        return "ip-form";
    }

    @PostMapping
    public String createIp(@ModelAttribute IpAddress ipAddress) {
        ipService.saveIp(ipAddress);
        return "redirect:/ips";
    }

    @GetMapping("/edit/{id}")
    public String editIp(@PathVariable Long id, Model model) {
        model.addAttribute("ipAddress", ipService.getIpByAddress(String.valueOf(id)));
        return "ip-form";
    }

    @PostMapping("/update/{id}")
    public String updateIp(@PathVariable Long id, @ModelAttribute IpAddress ipAddress) {
        ipAddress.setId(id);
        ipService.saveIp(ipAddress);
        return "redirect:/ips";
    }
}
