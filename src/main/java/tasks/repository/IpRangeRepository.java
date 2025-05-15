package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.IpRange;

public interface IpRangeRepository extends JpaRepository<IpRange, Long> {
    boolean existsByCidr(String cidr);
}
