package tasks.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tasks.entity.DnsRecord;
import tasks.repository.DnsRecordRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DnsRecordMigrationRunner {

    private final DnsRecordRepository dnsRecordRepository;

    @PostConstruct
    public void migrateFqdnValues() {
        List<DnsRecord> records = dnsRecordRepository.findAll();

        for (DnsRecord record : records) {
            if (record.getFqdn() == null || record.getFqdn().isEmpty()) {
                String fqdn = extractFqdn(record.getHost());
                record.setFqdn(fqdn);
                dnsRecordRepository.save(record);
            }
        }
    }

    private String extractFqdn(String host) {
        if (host == null || !host.contains(".")) return "";
        String[] parts = host.trim().toLowerCase().split("\\.");
        if (parts.length < 2) return host;
        return parts[parts.length - 2] + "." + parts[parts.length - 1];
    }
}
