package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.model.DnsDomain;
import tasks.repository.DnsDomainRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DnsService {

    private final DnsDomainRepository dnsDomainRepository;

    public List<DnsDomain> getAllDomains() {
        return dnsDomainRepository.findAll();
    }

    public Optional<DnsDomain> getDomainByName(String name) {
        return dnsDomainRepository.findAll().stream()
                .filter(domain -> domain.getDomainName().equals(name))
                .findFirst();
    }

    public DnsDomain saveDomain(DnsDomain domain) {
        return dnsDomainRepository.save(domain);
    }
}
