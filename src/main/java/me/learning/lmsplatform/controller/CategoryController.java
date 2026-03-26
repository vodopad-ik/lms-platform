package me.learning.lmsplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.CategoryDto;
import me.learning.lmsplatform.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management API")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", operationId = "categoryGetAll")
    public ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id", operationId = "categoryGetById")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter categories (JPQL)", operationId = "categoryFilterJpql")
    public ResponseEntity<Page<CategoryDto>> filterCategories(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String teacherDepartment,
        Pageable pageable) {
        return ResponseEntity.ok(categoryService.searchCategories(
            name, teacherDepartment, pageable, QueryMode.JPQL));
    }

    @GetMapping("/filter/native")
    @Operation(summary = "Filter categories (native query)", operationId = "categoryFilterNative")
    public ResponseEntity<Page<CategoryDto>> filterCategoriesNative(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String teacherDepartment,
        Pageable pageable) {
        return ResponseEntity.ok(categoryService.searchCategories(
            name, teacherDepartment, pageable, QueryMode.NATIVE));
    }

    @PostMapping
    @Operation(summary = "Create category", operationId = "categoryCreate")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(categoryService.createCategory(categoryDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", operationId = "categoryUpdate")
    public ResponseEntity<CategoryDto> updateCategory(
        @PathVariable Long id,
        @Valid @RequestBody CategoryDto categoryDetails) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", operationId = "categoryDelete")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
