package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.entity.IpRange;
import tasks.service.IpRangeService;

@Controller
@RequiredArgsConstructor
public class IpRangeController {

    private final IpRangeService ipRangeService;

    // CIDR 대역 생성 폼 반환 (모달용)
    @GetMapping("/ip-range/new")
    public String newIpRangeForm(Model model) {
        model.addAttribute("ipRange", new IpRange());
        return "ip-range-form"; // templates/ip-range-form.html
    }

    // CIDR 저장 API (ajax 요청용)
    @PostMapping("/api/ip-range")
    @ResponseBody
    public IpRange saveIpRange(@RequestBody IpRange ipRange) {
        return ipRangeService.createRangeWithIps(ipRange);
    }
}
