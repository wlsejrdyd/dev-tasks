package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tasks.entity.Project;
import tasks.model.IpAddress;
import tasks.model.DnsDomain;
import tasks.model.Attendance;
import tasks.repository.ProjectRepository;
import tasks.repository.IpAddressRepository;
import tasks.repository.DnsDomainRepository;
import tasks.repository.AttendanceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final IpAddressRepository ipAddressRepository;
    private final DnsDomainRepository dnsDomainRepository;
    private final AttendanceRepository attendanceRepository;

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public List<Project> getProjectOverview() {
        return projectRepository.findTop5ByOrderByStartDateDesc();
    }

    public List<IpAddress> getIpStatus() {
        return ipAddressRepository.findTop5ByOrderByIdDesc();
    }

    public List<DnsDomain> getDnsDomains() {
        return dnsDomainRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public List<Attendance> getAttendanceStatus() {
        return attendanceRepository.findTop5ByOrderByWorkDateDesc();
    }
}
