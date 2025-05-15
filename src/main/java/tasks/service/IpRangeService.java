package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.IpAddress;
import tasks.entity.IpRange;
import tasks.repository.IpAddressRepository;
import tasks.repository.IpRangeRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IpRangeService {

    private final IpRangeRepository ipRangeRepository;
    private final IpAddressRepository ipAddressRepository;

    public IpRange createRangeWithIps(IpRange ipRange) {
        // 대역 저장
        IpRange savedRange = ipRangeRepository.save(ipRange);

        // 대역 기반 IP 목록 생성
        List<String> ips = generateIpList(ipRange.getCidr());

        List<IpAddress> ipList = new ArrayList<>();
        for (String ip : ips) {
            IpAddress ipAddress = IpAddress.builder()
                    .ip(ip)
                    .range(savedRange)
                    .status(IpAddress.Status.DOWN)
                    .build();
            ipList.add(ipAddress);
        }

        ipAddressRepository.saveAll(ipList);
        return savedRange;
    }

    // CIDR 문자열 → 실제 IP 주소 리스트로 변환
    private List<String> generateIpList(String cidr) {
        List<String> result = new ArrayList<>();
        try {
            String[] parts = cidr.split("/");
            String ipPart = parts[0];
            int prefix = Integer.parseInt(parts[1]);

            InetAddress inetAddress = InetAddress.getByName(ipPart);
            byte[] addressBytes = inetAddress.getAddress();

            int ip = byteArrayToInt(addressBytes);
            int mask = ~((1 << (32 - prefix)) - 1);

            int network = ip & mask;
            int broadcast = network | ~mask;

            // host 범위 (network+1 ~ broadcast-1)
            for (int i = network + 1; i < broadcast; i++) {
                result.add(intToIp(i));
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException("잘못된 CIDR 형식입니다: " + cidr);
        }
        return result;
    }

    private int byteArrayToInt(byte[] bytes) {
        int result = 0;
        for (byte b : bytes) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }

    private String intToIp(int ip) {
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 8) & 0xFF,
                ip & 0xFF);
    }
}
