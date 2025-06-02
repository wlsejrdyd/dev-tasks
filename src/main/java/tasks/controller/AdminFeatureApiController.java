package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tasks.dto.CategorySaveRequest;
import tasks.dto.NoticeRequest;
import tasks.dto.PollRequest;
import tasks.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminFeatureApiController {

    private final AdminService adminService;

    @PostMapping("/categories")
    public void saveCategory(@RequestBody CategorySaveRequest request) {
        adminService.saveCategory(request);
    }

    @PostMapping("/notices")
    public void saveNotice(@RequestBody NoticeRequest request) {
        adminService.saveNotice(request);
    }

    @PostMapping("/poll")
    public void savePoll(@RequestBody PollRequest request) {
        adminService.savePoll(request);
    }
}
