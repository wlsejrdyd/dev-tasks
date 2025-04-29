package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.model.IpAddress;
import tasks.repository.IpAddressRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IpService {

    private final IpAddressRepository ipAddressRepository;

    public List<IpAddress> getAllIps() {
        return ipAddressRepository.findAll();
    }

    public Optional<IpAddress> getIpByAddress(String address) {
        return ipAddressRepository.findAll().stream()
                .filter(ip -> ip.getIp().equals(address))
                .findFirst();
    }

    public IpAddress saveIp(IpAddress ipAddress) {
        return ipAddressRepository.save(ipAddress);
    }
}
