package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tasks.entity.WeeklyCategory;
import tasks.service.WeeklyCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/weekly/categories")
@RequiredArgsConstructor
public class WeeklyCategoryController {

    private final WeeklyCategoryService weeklyCategoryService;

    @GetMapping
    public List<WeeklyCategory> getCategories() {
        return weeklyCategoryService.getAllCategories();
    }
}
