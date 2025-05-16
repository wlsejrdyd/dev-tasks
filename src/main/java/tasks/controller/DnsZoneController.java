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
                continue; // 알 수 없는 타입은 건너뜀
            }

            DnsRecord record = DnsRecord.builder()
                    .host(host)
                    .type(dnsType)
                    .ip(value)
                    .sslValid(false)
                    .description("Imported from .zone file")
                    .build();

            dnsRecordService.save(record);
        }

        return "ok";
    }

    @GetMapping("/dns/download")
    public void downloadZoneFile(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment;filename=dns_records.zone");

        List<DnsRecord> records = dnsRecordService.findAll();

        try (PrintWriter writer = response.getWriter()) {
            for (DnsRecord record : records) {
                writer.printf("%-20s IN %-6s %s\n",
                        record.getHost(),
                        record.getType().name(),
                        record.getIp());
            }
        }
    }
}

