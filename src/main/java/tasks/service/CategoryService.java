package tasks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tasks.entity.Category;
import tasks.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category save(String name) {
        if (categoryRepository.existsByName(name)) return null;
        return categoryRepository.save(Category.builder().name(name).build());
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
