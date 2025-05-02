package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.Dns;

public interface DnsRepository extends JpaRepository<Dns, Long> {
}
