package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.model.DnsDomain;

import java.util.List;

public interface DnsDomainRepository extends JpaRepository<DnsDomain, Long> {
    List<DnsDomain> findTop5ByOrderByCreatedAtDesc();
}
