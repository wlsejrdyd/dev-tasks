package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.entity.Dns;
import tasks.service.DnsService;

@Controller
@RequiredArgsConstructor
public class DnsController {

    private final DnsService dnsService;

    @GetMapping("/dns")
    public String dnsList(Model model) {
        model.addAttribute("dnsList", dnsService.getAllDns());
        return "dns";
    }

    @GetMapping("/dns/new")
    public String newDnsForm(Model model) {
        model.addAttribute("dns", new Dns());
        return "dns-new";
    }

    @PostMapping("/dns")
    public String createDns(@ModelAttribute Dns dns) {
        dnsService.saveDns(dns);
        return "redirect:/dns";
    }

    @GetMapping("/dns/edit/{id}")
    public String editDnsForm(@PathVariable Long id, Model model) {
        Dns dns = dnsService.getDnsById(id);
        model.addAttribute("dns", dns);
        return "dns-edit";
    }

    @PostMapping("/dns/update")
    public String updateDns(@ModelAttribute Dns dns) {
        dnsService.saveDns(dns);
        return "redirect:/dns";
    }

    @PostMapping("/dns/delete/{id}")
    public String deleteDns(@PathVariable Long id) {
        dnsService.deleteDns(id);
        return "redirect:/dns";
    }
}
