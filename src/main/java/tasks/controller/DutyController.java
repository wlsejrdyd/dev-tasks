package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tasks.dto.DutySaveRequest;
import tasks.dto.DutyStatResponse;
import tasks.entity.DutyCell;
import tasks.service.DutyService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DutyController {

    private final DutyService dutyService;

    @GetMapping("/duty")
    public String dutyPage() {
        return "duty";
    }

    @PostMapping("/api/duty/save")
    @ResponseBody
    public String save(@RequestBody DutySaveRequest request) {
        dutyService.saveTable(request);
        return "saved";
    }

    @GetMapping("/api/duty/stat/{yearMonth}")
    @ResponseBody
    public List<DutyStatResponse> getStats(@PathVariable String yearMonth) {
        return dutyService.getStatistics(yearMonth);
    }

    @GetMapping("/api/duty/load")
    @ResponseBody
    public List<DutyCell> loadCells(@RequestParam int year, @RequestParam int month) {
        return dutyService.getDutyCells(year, month);
    }
}
