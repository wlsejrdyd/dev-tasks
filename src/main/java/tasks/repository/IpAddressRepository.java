package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.model.IpAddress;

import java.util.List;

public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {
    List<IpAddress> findTop5ByOrderByIdDesc();
}
