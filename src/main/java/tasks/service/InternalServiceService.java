package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tasks.entity.InternalService;
import tasks.repository.InternalServiceRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InternalServiceService {

    private final InternalServiceRepository internalServiceRepository;

    public List<InternalService> findAll() {
        return internalServiceRepository.findAll();
    }

    public Optional<InternalService> findById(Long id) {
        return internalServiceRepository.findById(id);
    }

    @Transactional
    public InternalService save(InternalService service) {
        return internalServiceRepository.save(service);
    }

    @Transactional
    public InternalService update(Long id, InternalService updated) {
        return internalServiceRepository.findById(id).map(service -> {
            service.setName(updated.getName());
            service.setDescription(updated.getDescription());
            service.setUrl(updated.getUrl());
            service.setManager(updated.getManager());
            return service;
        }).orElseThrow(() -> new IllegalArgumentException("Service not found: " + id));
    }

    @Transactional
    public void delete(Long id) {
        internalServiceRepository.deleteById(id);
    }
}
