package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasks.entity.IpAddress;
import tasks.entity.User;
import tasks.repository.IpAddressRepository;
import tasks.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IpAddressService {

    private final IpAddressRepository ipAddressRepository;
    private final UserRepository userRepository;

    public List<IpAddress> findAll() {
        return ipAddressRepository.findAll();
    }

    public Optional<IpAddress> findById(Long id) {
        return ipAddressRepository.findById(id);
    }

    @Transactional
    public IpAddress save(IpAddress ipAddress) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        userOpt.ifPresent(user -> ipAddress.setWorker(user.getName()));
        ipAddress.setUpdatedAt(LocalDateTime.now());
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
