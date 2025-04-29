package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.model.DnsDomain;

public interface DnsDomainRepository extends JpaRepository<DnsDomain, Long> {
}
