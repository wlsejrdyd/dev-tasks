package tasks.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tasks.entity.DnsRecord;
import tasks.entity.DnsRecord.DnsType;
import tasks.service.DnsRecordService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DnsZoneController {

    private final DnsRecordService dnsRecordService;

    @PostMapping("/dns/upload")
    @ResponseBody
    public String uploadZoneFile(@RequestBody String content) {
        List<String> lines = Arrays.stream(content.split("\n"))
                .map(String::trim)
                .filter(line -> !line.startsWith(";") && !line.isEmpty())
                .toList();

        for (String line : lines) {
            String[] parts = line.split("\\s+");
            if (parts.length < 4) continue;

            String host = parts[0];
            String type = parts[2];
            String value = parts[3];

            DnsType dnsType;
            try {
                dnsType = DnsType.valueOf(type);
            } catch (IllegalArgumentException e) {
                continue;
            }

            String maindomain = extractDomain(host);

            DnsRecord record = DnsRecord.builder()
                    .host(host)
                    .maindomain(maindomain)
                    .type(dnsType)
                    .ip(value)
                    .sslValid(false)
                    .description("Imported from .zone file")
                    .build();

            dnsRecordService.save(record);
        }

        return "ok";
    }

    private String extractDomain(String host) {
        if (host == null || !host.contains(".")) return "";
        host = host.trim().toLowerCase();
        if (host.startsWith("http://")) host = host.substring(7);
        if (host.startsWith("https://")) host = host.substring(8);
        int slashIndex = host.indexOf('/');
        if (slashIndex > -1) host = host.substring(0, slashIndex);
        String[] parts = host.split("\\.");
        if (parts.length < 2) return host;
        return parts[parts.length - 2] + "." + parts[parts.length - 1];
    }
}
