package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import tasks.entity.IpAddress;
import tasks.repository.IpAddressRepository;
import tasks.repository.IpRangeRepository;
import tasks.service.IpAddressService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class IpController {

    private final IpAddressRepository ipAddressRepository;
    private final IpRangeRepository ipRangeRepository;
    private final IpAddressService ipAddressService;

    @GetMapping("/ip")
    public String ipList(@RequestParam(required = false) String keyword, Model model) {
        List<IpAddress> ipList = null;
        if (keyword != null && !keyword.isBlank()) {
            ipList = ipAddressRepository.findByIpContaining(keyword);
        }

        model.addAttribute("ipList", ipList);
        model.addAttribute("ipRanges", ipRangeRepository.findAll(Sort.by(Sort.Direction.ASC, "cidr")));

        return "ip-address";
    }

    @GetMapping("/ip/edit/{id}")
    public String editIp(@PathVariable Long id, Model model) {
        IpAddress ipAddress = ipAddressService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 IP가 존재하지 않습니다: " + id));
        model.addAttribute("ipAddress", ipAddress);
        return "ip-form";
    }
}
