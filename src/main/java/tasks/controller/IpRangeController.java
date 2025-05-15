package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tasks.entity.IpRange;
import tasks.service.IpRangeService;

@Controller
@RequiredArgsConstructor
public class IpRangeController {

    private final IpRangeService ipRangeService;

    @GetMapping("/ip-range/new")
    public String newIpRangeForm(org.springframework.ui.Model model) {
        model.addAttribute("ipRange", new IpRange());
        return "ip-range-form";
    }

    @PostMapping("/api/ip-range")
    @ResponseBody
    public IpRange saveIpRange(@RequestBody IpRange ipRange) {
        return ipRangeService.createRangeWithIps(ipRange);
    }

    @DeleteMapping("/api/ip-range/{id}")
    @ResponseBody
    public void deleteRange(@PathVariable Long id) {
        ipRangeService.deleteRange(id);
    }
}
