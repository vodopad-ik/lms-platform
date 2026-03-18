package me.learning.lmsplatform.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.CategoryQueryCache;
import me.learning.lmsplatform.cache.CategoryQueryCacheKey;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.CategoryDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.mapper.CategoryMapper;
import me.learning.lmsplatform.model.Category;
import me.learning.lmsplatform.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private static final String NOT_FOUND_MSG = "Category not found with id: ";

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;
  private final CategoryQueryCache categoryQueryCache;
  private final CacheInvalidationService cacheInvalidationService;

  public List<CategoryDto> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(categoryMapper::mapToDto)
        .toList();
  }

  public CategoryDto getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .map(categoryMapper::mapToDto)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
  }

  public CategoryDto createCategory(CategoryDto categoryDto) {
    Category category = categoryMapper.mapToEntity(categoryDto);
    Category saved = categoryRepository.save(category);
    cacheInvalidationService.onCategoryChanged();
    return categoryMapper.mapToDto(saved);
  }

  public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
    Category existing = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
    existing.setName(categoryDto.getName());
    Category saved = categoryRepository.save(existing);
    cacheInvalidationService.onCategoryChanged();
    return categoryMapper.mapToDto(saved);
  }

  public void deleteCategory(Long id) {
    categoryRepository.deleteById(id);
    cacheInvalidationService.onCategoryChanged();
  }

  public Page<CategoryDto> searchCategories(
      String nameFilter,
      String teacherDepartment,
      Pageable pageable,
      QueryMode queryMode) {
    String normalizedNameFilter = normalizeLike(nameFilter);
    String normalizedTeacherDepartment = normalizeLike(teacherDepartment);
    CategoryQueryCacheKey key = CategoryQueryCacheKey.from(
        queryMode, normalizedNameFilter, normalizedTeacherDepartment, pageable);
    return categoryQueryCache.getOrLoad(
        key,
        () -> fetchCategoriesWithFilters(
            normalizedNameFilter, normalizedTeacherDepartment, pageable, queryMode));
  }

  private Page<CategoryDto> fetchCategoriesWithFilters(
      String nameFilter,
      String teacherDepartment,
      Pageable pageable,
      QueryMode queryMode) {
    if (queryMode == QueryMode.NATIVE) {
      Page<Category> nativePage = categoryRepository.findWithFiltersNative(
          nameFilter, teacherDepartment, pageable);
      return mapCategoryPage(nativePage, pageable);
    }
    return categoryRepository.findWithFilters(nameFilter, teacherDepartment, pageable)
        .map(categoryMapper::mapToDto);
  }

  private Page<CategoryDto> mapCategoryPage(Page<Category> categoryPage, Pageable pageable) {
    return new PageImpl<>(
        categoryPage.stream()
            .map(categoryMapper::mapToDto)
            .toList(),
        pageable,
        categoryPage.getTotalElements());
  }

  private static String normalizeLike(String value) {
    return value == null ? "" : value;
  }
}

