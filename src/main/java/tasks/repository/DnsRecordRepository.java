package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.DnsRecord;
import tasks.entity.DnsRecord.DnsType;

import java.util.List;

public interface DnsRecordRepository extends JpaRepository<DnsRecord, Long> {

    List<DnsRecord> findByHostContainingIgnoreCaseOrIpContainingIgnoreCase(String host, String ip);

    List<DnsRecord> findByType(DnsType type);

    List<DnsRecord> findBySslValid(boolean sslValid);

    List<DnsRecord> findByMaindomain(String maindomain);
}
