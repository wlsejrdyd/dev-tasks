package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.DnsRecord;
import tasks.entity.DnsRecord.DnsType;
import tasks.repository.DnsRecordRepository;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;

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
                boolean valid = checkSslValidity(record.getHost());
                record.setSslValid(valid);
                dnsRecordRepository.save(record);
            } catch (Exception e) {
                record.setSslValid(false);
                dnsRecordRepository.save(record);
            }
        }
    }

    private boolean checkSslValidity(String host) {
        try (var socket = SSLSocketFactory.getDefault().createSocket(host, 443)) {
            ((SSLSocket) socket).startHandshake();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
