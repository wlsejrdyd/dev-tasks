package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.model.DnsDomain;
import tasks.service.DnsService;

@Controller
@RequestMapping("/dns")
@RequiredArgsConstructor
public class DnsController {

    private final DnsService dnsService;

    @GetMapping
    public String listDnsDomains(Model model) {
        model.addAttribute("domains", dnsService.getAllDomains());
        return "dns";
    }

    @GetMapping("/new")
    public String newDomainForm(Model model) {
        model.addAttribute("dnsDomain", new DnsDomain());
        return "dns-form";
    }

    @PostMapping
    public String createDomain(@ModelAttribute DnsDomain dnsDomain) {
        dnsService.saveDomain(dnsDomain);
        return "redirect:/dns";
    }

    @GetMapping("/edit/{id}")
    public String editDomain(@PathVariable Long id, Model model) {
        model.addAttribute("dnsDomain", dnsService.getDomainByName(String.valueOf(id)));
        return "dns-form";
    }

    @PostMapping("/update/{id}")
    public String updateDomain(@PathVariable Long id, @ModelAttribute DnsDomain dnsDomain) {
        dnsDomain.setId(id);
        dnsService.saveDomain(dnsDomain);
        return "redirect:/dns";
    }
}
