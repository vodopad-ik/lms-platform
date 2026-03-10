package me.learning.lmsplatform.mapper;

import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.CategoryDto;
import me.learning.lmsplatform.model.Category;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    public CategoryDto mapToDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category mapToEntity(CategoryDto dto) {
        if (dto == null) return null;
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
