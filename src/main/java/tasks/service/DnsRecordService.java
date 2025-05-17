package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.DnsRecord;
import tasks.entity.DnsRecord.DnsType;
import tasks.repository.DnsRecordRepository;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DnsRecordService {

    private final DnsRecordRepository dnsRecordRepository;

    public List<DnsRecord> findAll() {
        return dnsRecordRepository.findAll();
    }

    public Optional<DnsRecord> findById(Long id) {
        return dnsRecordRepository.findById(id);
    }

    public DnsRecord save(DnsRecord dnsRecord) {
        return dnsRecordRepository.save(dnsRecord);
    }

    public void delete(Long id) {
        dnsRecordRepository.deleteById(id);
    }

    public List<DnsRecord> searchBy(String keyword, DnsType type, Boolean sslValid) {
        List<DnsRecord> all = dnsRecordRepository.findAll();

        return all.stream()
                .filter(r -> keyword == null || r.getHost().toLowerCase().contains(keyword.toLowerCase()) || r.getIp().contains(keyword))
                .filter(r -> type == null || r.getType() == type)
                .filter(r -> sslValid == null || r.isSslValid() == sslValid)
                .collect(Collectors.toList());
    }

    public String generateZoneFile(String maindomain) {
        List<DnsRecord> records = dnsRecordRepository.findByMaindomain(maindomain);

        return records.stream()
                .map(r -> String.format("%s\tIN\t%s\t%s",
                        r.getHost(), r.getType(), r.getIp()))
                .collect(Collectors.joining("\n"));
    }

    public List<DnsRecord> search(String keyword) {
        return dnsRecordRepository.findAll().stream()
                .filter(r -> keyword == null
                        || r.getHost().toLowerCase().contains(keyword.toLowerCase())
                        || r.getIp().contains(keyword))
                .collect(Collectors.toList());
    }

    public void checkAndUpdateAllSsl() {
        List<DnsRecord> records = dnsRecordRepository.findAll();

        for (DnsRecord record : records) {
            try {
                boolean valid = checkSslValidity(record.getHost(), record.getMaindomain());
                record.setSslValid(valid);
                dnsRecordRepository.save(record);
            } catch (Exception e) {
                record.setSslValid(false);
                dnsRecordRepository.save(record);
            }
        }
    }

    public void checkAndUpdateAllSslExpiry() {
        List<DnsRecord> records = dnsRecordRepository.findAll();

        for (DnsRecord record : records) {
            try {
                LocalDate expiry = getSslExpiryDate(record.getHost(), record.getMaindomain());
                record.setSslExpiryDate(expiry);
                dnsRecordRepository.save(record);
            } catch (Exception e) {
                record.setSslExpiryDate(null);
                dnsRecordRepository.save(record);
            }
        }
    }

    private boolean checkSslValidity(String host, String maindomain) {
        String fqdn = buildFqdn(host, maindomain);
        try (var socket = SSLSocketFactory.getDefault().createSocket(fqdn, 443)) {
            ((SSLSocket) socket).startHandshake();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private LocalDate getSslExpiryDate(String host, String maindomain) throws Exception {
        String fqdn = buildFqdn(host, maindomain);
        try (var socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(fqdn, 443)) {
            socket.startHandshake();
            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate) session.getPeerCertificates()[0];
            Instant instant = cert.getNotAfter().toInstant();
            return instant.atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    private String buildFqdn(String host, String maindomain) {
        if (host == null || host.isBlank()) return maindomain;
        return host + "." + maindomain;
    }

    public void importZoneFile(String content, String maindomain) {
        String[] lines = content.split("\\r?\\n");
        String lastHost = null;

        for (String line : lines) {
            if (line.isBlank() || line.startsWith(";")) continue;

            String[] parts = line.trim().split("\\s+");
            if (parts.length < 4) continue;

            String host = parts[0];
            String type = parts[2];
            String ip = parts[3];

            // ✅ host 처리 로직
            if (host.equals("@")) {
                host = maindomain;
            } else if (host.equals("")) {
                host = lastHost;
            } else if (host.endsWith(".")) {
                host = host.substring(0, host.length() - 1);
            }

            lastHost = host;

            try {
                DnsType dnsType = DnsType.valueOf(type);

                DnsRecord record = DnsRecord.builder()
                        .host(host)
                        .maindomain(maindomain)
                        .type(dnsType)
                        .ip(ip)
                        .sslValid(false)
                        .description("Imported from .zone file")
                        .build();

                dnsRecordRepository.save(record);
            } catch (IllegalArgumentException ignored) {
                // 무시
            }
        }
    }

    public long countUniqueMaindomains() {
        return dnsRecordRepository.findAll().stream()
                .map(DnsRecord::getMaindomain)
                .filter(m -> m != null && !m.isBlank())
                .distinct()
                .count();
    }
}
