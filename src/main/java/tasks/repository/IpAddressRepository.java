package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.IpAddress;

import java.util.List;

public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {

    List<IpAddress> findByIpContaining(String keyword);

    List<IpAddress> findByIpContainingIgnoreCase(String keyword);

    List<IpAddress> findByStatus(IpAddress.Status status);

    boolean existsByIp(String ip); // ✅ 중복 확인 메서드 추가
}
