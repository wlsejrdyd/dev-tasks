package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tasks.entity.DnsRecord;
import tasks.entity.DnsRecord.DnsType;
import tasks.service.DnsRecordService;

@Controller
@RequiredArgsConstructor
public class DnsController {

    private final DnsRecordService dnsRecordService;

    @GetMapping("/dns")
    public String dnsPage(Model model) {
        model.addAttribute("dnsList", dnsRecordService.findAll());
        model.addAttribute("types", DnsType.values());
        return "dns";
    }
}

