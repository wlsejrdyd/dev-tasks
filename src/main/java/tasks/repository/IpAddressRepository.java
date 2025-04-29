package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.model.IpAddress;

public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {
}
