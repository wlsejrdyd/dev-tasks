package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.entity.ServiceEntity;
import tasks.service.ServiceService;

@Controller
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping("/services")
    public String serviceList(Model model) {
        model.addAttribute("services", serviceService.getAllServices());
        return "service";
    }

    @GetMapping("/services/new")
    public String newServiceForm(Model model) {
        model.addAttribute("service", new ServiceEntity());
        return "service-new";
    }

    @PostMapping("/services")
    public String createService(@ModelAttribute("service") ServiceEntity serviceEntity) {
        serviceService.saveService(serviceEntity);
        return "redirect:/services";
    }

    @GetMapping("/services/edit/{id}")
    public String editServiceForm(@PathVariable Long id, Model model) {
        ServiceEntity service = serviceService.getServiceById(id);
        model.addAttribute("service", service);
        return "service-edit";
    }

    @PostMapping("/services/update")
    public String updateService(@ModelAttribute ServiceEntity serviceEntity) {
        serviceService.saveService(serviceEntity);
        return "redirect:/services";
    }

    @PostMapping("/services/delete/{id}")
    public String deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return "redirect:/services";
    }
}
