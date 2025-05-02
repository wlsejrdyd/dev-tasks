package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.Ip;

public interface IpRepository extends JpaRepository<Ip, Long> {
}
