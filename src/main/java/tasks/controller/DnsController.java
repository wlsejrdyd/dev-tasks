package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tasks.entity.DnsRecord;
import tasks.entity.DnsRecord.DnsType;
import tasks.service.DnsRecordService;
import tasks.utils.DnsThymeleafUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DnsController {

    private final DnsRecordService dnsRecordService;
    private final DnsThymeleafUtils dns; // Thymeleaf util로 주입

    @GetMapping("/dns")
    public String dnsPage(@RequestParam(required = false) String keyword,
                          @RequestParam(required = false) DnsType type,
                          @RequestParam(required = false) Boolean sslValid,
                          Model model) {

        List<DnsRecord> dnsList = dnsRecordService.searchBy(keyword, type, sslValid);

        Map<String, List<DnsRecord>> grouped = dnsList.stream()
                .collect(Collectors.groupingBy(record -> dns.extractDomain(record.getHost())));

        model.addAttribute("dnsListGroupedByDomain", grouped.entrySet());
        model.addAttribute("types", DnsType.values());
        return "dns";
    }
}
