package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.WeeklyCategory;
import tasks.repository.WeeklyCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyCategoryService {

    private final WeeklyCategoryRepository weeklyCategoryRepository;

    public List<WeeklyCategory> getAllCategories() {
        return weeklyCategoryRepository.findAllByOrderBySortOrderAsc();
    }
}
