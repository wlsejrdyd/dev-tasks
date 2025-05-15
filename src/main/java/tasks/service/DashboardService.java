package tasks.service;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    // TODO: 현재는 더미 데이터입니다. 추후 Repository 주입 예정

    public int getProjectCount() {
        return 0; // TODO: ProjectRepository.count() 로 교체
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
