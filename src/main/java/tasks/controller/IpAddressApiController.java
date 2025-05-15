package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasks.entity.IpAddress;
import tasks.service.IpAddressService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ip")
public class IpAddressApiController {

    private final IpAddressService ipAddressService;

    @PostMapping
    public ResponseEntity<IpAddress> create(@RequestBody IpAddress ipAddress) {
        IpAddress saved = ipAddressService.save(ipAddress);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IpAddress> update(@PathVariable Long id, @RequestBody IpAddress updatedIp) {
        return ipAddressService.findById(id)
                .map(ip -> {
                    ip.setIp(updatedIp.getIp());
                    ip.setDescription(updatedIp.getDescription());
                    ip.setStatus(updatedIp.getStatus());
                    ip.setRequestDate(updatedIp.getRequestDate());
                    ip.setRequester(updatedIp.getRequester());
                    ip.setDepartment(updatedIp.getDepartment());
                    ip.setContact(updatedIp.getContact());
                    ip.setWorker(updatedIp.getWorker());
                    ip.setProjectId(updatedIp.getProjectId());
                    return ResponseEntity.ok(ipAddressService.save(ip));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ipAddressService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<IpAddress>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ipAddressService.searchByKeyword(keyword));
    }
}
