package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.repository.ProjectRepository;
import tasks.repository.InternalServiceRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final DnsRecordService dnsRecordService;
    private final InternalServiceRepository internalServiceRepository;

    public long getProjectCount() {
        return projectRepository.count();
    }

    public long getProjectCountByStatus(String status) {
        return projectRepository.countByStatus(status);
    }

    public int getIpCount() {
        return 0; // TODO: IpRepository.count() 로 교체
    }

    public long getDnsCount() {
        return dnsRecordService.countUniqueMaindomains(); // ✅ maindomain 기준
    }

    public int getServiceCount() {
        return 0; // TODO: ServiceListRepository.count() 로 교체
    }

    // ✅ 추가된 내부 서비스 총합 메서드
    public long getInternalServiceTotalCount() {
        return internalServiceRepository.count();
    }
}
