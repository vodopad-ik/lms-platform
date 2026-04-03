package me.learning.lmsplatform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.cache.CategoryQueryCache;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.CategoryDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.mapper.CategoryMapper;
import me.learning.lmsplatform.model.Category;
import me.learning.lmsplatform.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CategoryMapper categoryMapper;

  @Mock
  private CategoryQueryCache categoryQueryCache;

  @Mock
  private CacheInvalidationService cacheInvalidationService;

  @InjectMocks
  private CategoryService categoryService;

  @Test
  void getAllCategories_shouldReturnAllCategories() {
    Category category1 = new Category();
    category1.setId(1L);
    category1.setName("Category 1");

    Category category2 = new Category();
    category2.setId(2L);
    category2.setName("Category 2");

    when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));
    when(categoryMapper.mapToDto(category1)).thenReturn(CategoryDto.builder().id(1L).name("Category 1").build());
    when(categoryMapper.mapToDto(category2)).thenReturn(CategoryDto.builder().id(2L).name("Category 2").build());

    List<CategoryDto> result = categoryService.getAllCategories();

    assertEquals(2, result.size());
    assertEquals("Category 1", result.get(0).getName());
    assertEquals("Category 2", result.get(1).getName());
  }

  @Test
  void getCategoryById_whenExists_shouldReturnCategory() {
    Category category = new Category();
    category.setId(1L);
    category.setName("Test Category");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(categoryMapper.mapToDto(category)).thenReturn(CategoryDto.builder().id(1L).name("Test Category").build());

    CategoryDto result = categoryService.getCategoryById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("Test Category", result.getName());
  }

  @Test
  void getCategoryById_whenNotExists_shouldThrowException() {
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(999L));
  }

  @Test
  void createCategory_shouldSaveAndReturnCategory() {
    CategoryDto dto = CategoryDto.builder().name("New Category").build();
    Category entity = new Category();
    entity.setName("New Category");
    Category saved = new Category();
    saved.setId(1L);
    saved.setName("New Category");

    when(categoryMapper.mapToEntity(dto)).thenReturn(entity);
    when(categoryRepository.save(entity)).thenReturn(saved);
    when(categoryMapper.mapToDto(saved)).thenReturn(CategoryDto.builder().id(1L).name("New Category").build());

    CategoryDto result = categoryService.createCategory(dto);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(cacheInvalidationService, times(1)).onCategoryChanged();
  }

  @Test
  void updateCategory_whenExists_shouldUpdateAndReturnCategory() {
    CategoryDto dto = CategoryDto.builder().name("Updated Category").build();
    Category existing = new Category();
    existing.setId(1L);
    existing.setName("Old Name");
    Category saved = new Category();
    saved.setId(1L);
    saved.setName("Updated Category");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(categoryRepository.save(existing)).thenReturn(saved);
    when(categoryMapper.mapToDto(saved)).thenReturn(CategoryDto.builder().id(1L).name("Updated Category").build());

    CategoryDto result = categoryService.updateCategory(1L, dto);

    assertEquals("Updated Category", result.getName());
    verify(cacheInvalidationService, times(1)).onCategoryChanged();
  }

  @Test
  void updateCategory_whenNotExists_shouldThrowException() {
    CategoryDto dto = CategoryDto.builder().name("Updated").build();
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(999L, dto));
  }

  @Test
  void deleteCategory_shouldDeleteAndInvalidateCache() {
    categoryService.deleteCategory(1L);

    verify(categoryRepository, times(1)).deleteById(1L);
    verify(cacheInvalidationService, times(1)).onCategoryChanged();
  }

  @Test
  void searchCategories_withJpqlMode_shouldReturnResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Category category = new Category();
    category.setId(1L);
    category.setName("Test");
    Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);

    when(categoryQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(categoryRepository.findWithFilters("test", "dept", pageable)).thenReturn(page);
    when(categoryMapper.mapToDto(category)).thenReturn(CategoryDto.builder().id(1L).name("Test").build());

    Page<CategoryDto> result = categoryService.searchCategories("test", "dept", pageable, QueryMode.JPQL);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void searchCategories_withNativeMode_shouldReturnResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Category category = new Category();
    category.setId(1L);
    category.setName("Test");
    Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);

    when(categoryQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(categoryRepository.findWithFiltersNative("test", "dept", pageable)).thenReturn(page);
    when(categoryMapper.mapToDto(category)).thenReturn(CategoryDto.builder().id(1L).name("Test").build());

    Page<CategoryDto> result = categoryService.searchCategories("test", "dept", pageable, QueryMode.NATIVE);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void searchCategories_withNullFilters_shouldNormalizeToEmptyString() {
    Pageable pageable = PageRequest.of(0, 10);
    Category category = new Category();
    category.setId(1L);
    category.setName("Test");
    Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);

    when(categoryQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(categoryRepository.findWithFilters("", "", pageable)).thenReturn(page);
    when(categoryMapper.mapToDto(category)).thenReturn(CategoryDto.builder().id(1L).name("Test").build());

    Page<CategoryDto> result = categoryService.searchCategories(null, null, pageable, QueryMode.JPQL);

    assertEquals(1, result.getTotalElements());
  }
}
