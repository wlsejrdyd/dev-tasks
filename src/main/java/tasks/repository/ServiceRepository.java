package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tasks.entity.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
}
