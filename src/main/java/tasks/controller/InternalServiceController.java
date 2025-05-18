package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasks.entity.InternalService;
import tasks.service.InternalServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/internal-services")
@RequiredArgsConstructor
public class InternalServiceController {

    private final InternalServiceService internalServiceService;

    @GetMapping
    public ResponseEntity<List<InternalService>> getAllServices() {
        return ResponseEntity.ok(internalServiceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalService> getService(@PathVariable Long id) {
        return internalServiceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InternalService> createService(@RequestBody InternalService service) {
        return ResponseEntity.ok(internalServiceService.save(service));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternalService> updateService(@PathVariable Long id, @RequestBody InternalService service) {
        return ResponseEntity.ok(internalServiceService.update(id, service));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        internalServiceService.delete(id);
        return ResponseEntity.ok().build();
    }
}
