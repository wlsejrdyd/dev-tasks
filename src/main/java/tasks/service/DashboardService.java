package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;

    public long getProjectCount() {
        return projectRepository.count();
    }

    public long getProjectCountByStatus(String status) {
        return projectRepository.countByStatus(status);
    }

    public int getIpCount() {
        return 0; // TODO: IpRepository.count() 로 교체
    }

    public int getDnsCount() {
        return 0; // TODO: DnsRepository.count() 로 교체
    }

    public int getServiceCount() {
        return 0; // TODO: ServiceListRepository.count() 로 교체
    }
}
