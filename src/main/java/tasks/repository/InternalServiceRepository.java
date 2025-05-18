package tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasks.entity.InternalService;

@Repository
public interface InternalServiceRepository extends JpaRepository<InternalService, Long> {
}
