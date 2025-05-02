package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.Dns;
import tasks.repository.DnsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DnsService {

    private final DnsRepository dnsRepository;

    public List<Dns> getAllDns() {
        return dnsRepository.findAll();
    }

    public Dns getDnsById(Long id) {
        return dnsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 도메인이 존재하지 않습니다. id=" + id));
    }

    public void saveDns(Dns dns) {
        dnsRepository.save(dns);
    }

    public void deleteDns(Long id) {
        dnsRepository.deleteById(id);
    }
}
