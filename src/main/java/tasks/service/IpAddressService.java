package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasks.entity.IpAddress;
import tasks.repository.IpAddressRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IpAddressService {

    private final IpAddressRepository ipAddressRepository;

    public List<IpAddress> findAll() {
        return ipAddressRepository.findAll();
    }

    public Optional<IpAddress> findById(Long id) {
        return ipAddressRepository.findById(id);
    }

    public IpAddress save(IpAddress ipAddress) {
        return ipAddressRepository.save(ipAddress);
    }

    public void deleteById(Long id) {
        ipAddressRepository.deleteById(id);
    }

    public List<IpAddress> searchByKeyword(String keyword) {
        return ipAddressRepository.findByIpContainingIgnoreCase(keyword);
    }

    public List<IpAddress> findByStatus(IpAddress.Status status) {
        return ipAddressRepository.findByStatus(status);
    }
}
