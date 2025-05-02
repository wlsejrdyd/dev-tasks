package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.ServiceEntity;
import tasks.repository.ServiceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public ServiceEntity getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 서비스가 존재하지 않습니다. id=" + id));
    }

    public void saveService(ServiceEntity serviceEntity) {
        serviceRepository.save(serviceEntity);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
