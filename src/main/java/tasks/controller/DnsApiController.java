package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tasks.entity.DnsRecord;
import tasks.service.DnsRecordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dns")
public class DnsApiController {

    private final DnsRecordService dnsRecordService;

    @GetMapping
    public List<DnsRecord> getAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return dnsRecordService.search(keyword);
        }
        return dnsRecordService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DnsRecord> getOne(@PathVariable Long id) {
        return dnsRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DnsRecord> create(@RequestBody DnsRecord dnsRecord) {
        return ResponseEntity.ok(dnsRecordService.save(dnsRecord));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DnsRecord> update(@PathVariable Long id, @RequestBody DnsRecord updated) {
        return dnsRecordService.findById(id)
                .map(record -> {
                    record.setHost(updated.getHost());
                    record.setMaindomain(updated.getMaindomain());
                    record.setType(updated.getType());
                    record.setIp(updated.getIp());
                    record.setDescription(updated.getDescription());
                    record.setSslValid(updated.isSslValid());
                    record.setSslExpiryDate(updated.getSslExpiryDate());
                    return ResponseEntity.ok(dnsRecordService.save(record));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dnsRecordService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ssl-check")
    public ResponseEntity<?> checkSslValidityForAll() {
        dnsRecordService.checkAndUpdateAllSsl();
        return ResponseEntity.ok("SSL 상태 갱신 완료");
    }

    @PostMapping("/ssl-expiry-check")
    public ResponseEntity<?> checkSslExpiryForAll() {
        dnsRecordService.checkAndUpdateAllSslExpiry();
        return ResponseEntity.ok("SSL 만료일 갱신 완료");
    }
}
