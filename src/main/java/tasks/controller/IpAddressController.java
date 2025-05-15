package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.entity.IpAddress;
import tasks.service.IpAddressService;

import java.util.List;

//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/ip")
//public class IpAddressController {
//
//    private final IpAddressService ipAddressService;
//
//    @GetMapping
//    public String list(Model model) {
//        List<IpAddress> ipList = ipAddressService.findAll();
//        model.addAttribute("ipList", ipList);
//        return "ip-address";
//    }
//
//    @GetMapping("/new")
//    public String createForm(Model model) {
//        model.addAttribute("ipAddress", new IpAddress());
//        model.addAttribute("statuses", IpAddress.Status.values());
//        return "ip-form";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String editForm(@PathVariable Long id, Model model) {
//        IpAddress ip = ipAddressService.findById(id).orElseThrow();
//        model.addAttribute("ipAddress", ip);
//        model.addAttribute("statuses", IpAddress.Status.values());
//        return "ip-form";
//    }
//}
