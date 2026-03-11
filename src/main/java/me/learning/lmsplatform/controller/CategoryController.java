package me.learning.lmsplatform.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.CategoryDto;
import me.learning.lmsplatform.mapper.CategoryMapper;
import me.learning.lmsplatform.repository.CategoryRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::mapToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public CategoryDto getCategory(@PathVariable Long id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.findById(id)
                .map(categoryMapper::mapToDto)
                .orElse(null);
    }

    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        return categoryMapper.mapToDto(categoryRepository.save(
                categoryMapper.mapToEntity(categoryDto)));
    }

    @PutMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody CategoryDto categoryDetails) {
        if (id == null || categoryDetails == null) {
            return null;
        }
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(categoryDetails.getName());
                    return categoryMapper.mapToDto(categoryRepository.save(category));
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        if (id != null) {
            categoryRepository.deleteById(id);
        }
    }
}
