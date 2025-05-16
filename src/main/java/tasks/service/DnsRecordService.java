package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.DnsRecord;
import tasks.entity.DnsRecord.DnsType;
import tasks.repository.DnsRecordRepository;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.List;
import java.util.Optional;

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

    public List<DnsRecord> search(String keyword) {
        return dnsRecordRepository.findByHostContainingIgnoreCaseOrIpContainingIgnoreCase(keyword, keyword);
    }

    public void checkAndUpdateAllSsl() {
        List<DnsRecord> records = findAll();
        for (DnsRecord record : records) {
            String host = extractDomain(record.getHost());
            boolean valid = isSslValid(host);
            record.setSslValid(valid);
            save(record);
        }
    }

    private boolean isSslValid(String host) {
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(host, 443)) {
                socket.setSoTimeout(3000);
                socket.startHandshake();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private String extractDomain(String host) {
        host = host.trim().toLowerCase();
        if (host.startsWith("http://")) host = host.substring(7);
        if (host.startsWith("https://")) host = host.substring(8);
        int slashIndex = host.indexOf('/');
        if (slashIndex > -1) host = host.substring(0, slashIndex);
        return host;
    }
}

