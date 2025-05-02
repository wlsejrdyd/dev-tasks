package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.entity.Ip;
import tasks.service.IpService;

@Controller
@RequiredArgsConstructor
public class IpController {

    private final IpService ipService;

    @GetMapping("/ips")
    public String ipList(Model model) {
        model.addAttribute("ips", ipService.getAllIps());
        return "ip";
    }

    @GetMapping("/ips/new")
    public String newIpForm(Model model) {
        model.addAttribute("ip", new Ip());
        return "ip-new";
    }

    @PostMapping("/ips")
    public String createIp(@ModelAttribute Ip ip) {
        ipService.saveIp(ip);
        return "redirect:/ips";
    }

    @GetMapping("/ips/edit/{id}")
    public String editIpForm(@PathVariable Long id, Model model) {
        Ip ip = ipService.getIpById(id);
        model.addAttribute("ip", ip);
        return "ip-edit";
    }

    @PostMapping("/ips/update")
    public String updateIp(@ModelAttribute Ip ip) {
        ipService.saveIp(ip);
        return "redirect:/ips";
    }

    @PostMapping("/ips/delete/{id}")
    public String deleteIp(@PathVariable Long id) {
        ipService.deleteIp(id);
        return "redirect:/ips";
    }
}
