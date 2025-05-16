package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tasks.entity.DnsRecord;
import tasks.entity.DnsRecord.DnsType;
import tasks.service.DnsRecordService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DnsController {

    private final DnsRecordService dnsRecordService;

    @GetMapping("/dns")
    public String dnsPage(@RequestParam(required = false) String keyword,
                          @RequestParam(required = false) DnsType type,
                          @RequestParam(required = false) Boolean sslValid,
                          Model model) {

        List<DnsRecord> dnsList = dnsRecordService.searchBy(keyword, type, sslValid);

        Map<String, List<DnsRecord>> grouped = dnsList.stream()
                .collect(Collectors.groupingBy(r -> r.getMaindomain() != null ? r.getMaindomain() : "기타"));

        model.addAttribute("dnsListGroupedByDomain", grouped);
        model.addAttribute("types", DnsType.values());
        return "dns";
    }

    @GetMapping("/dns/download")
    public ResponseEntity<Resource> downloadZoneFile(@RequestParam String maindomain) {
        String zoneData = dnsRecordService.generateZoneFile(maindomain);
        ByteArrayResource resource = new ByteArrayResource(zoneData.getBytes());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + maindomain + ".zone")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
}
