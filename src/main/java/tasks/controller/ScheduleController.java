package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tasks.dto.ScheduleRequest;
import tasks.entity.Schedule;
import tasks.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public void save(@RequestBody ScheduleRequest request) {
        scheduleService.save(request);
    }

    @GetMapping
    public List<Schedule> getMonthlySchedules(@RequestParam int year, @RequestParam int month) {
        return scheduleService.findByYearAndMonth(year, month);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody ScheduleRequest request) {
        scheduleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        scheduleService.delete(id);
    }
}
