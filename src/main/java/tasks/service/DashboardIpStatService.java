package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.IpAddress;
import tasks.repository.IpAddressRepository;
import tasks.repository.IpRangeRepository;

@Service
@RequiredArgsConstructor
public class DashboardIpStatService {

    private final IpAddressRepository ipAddressRepository;
    private final IpRangeRepository ipRangeRepository;

    public long getIpRangeCount() {
        return ipRangeRepository.count();
    }

    public long getIpUpCount() {
        return ipAddressRepository.findByStatus(IpAddress.Status.UP).size();
    }

    public long getIpDownCount() {
        return ipAddressRepository.findByStatus(IpAddress.Status.DOWN).size();
    }
}
