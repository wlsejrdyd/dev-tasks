package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.Ip;
import tasks.repository.IpRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IpService {

    private final IpRepository ipRepository;

    public List<Ip> getAllIps() {
        return ipRepository.findAll();
    }

    public Ip getIpById(Long id) {
        return ipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 IP가 존재하지 않습니다. id=" + id));
    }

    public void saveIp(Ip ip) {
        ipRepository.save(ip);
    }

    public void deleteIp(Long id) {
        ipRepository.deleteById(id);
    }
}
