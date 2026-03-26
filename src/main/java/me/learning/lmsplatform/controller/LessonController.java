package me.learning.lmsplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.service.LessonService;
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
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Lesson management API")
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    @Operation(summary = "Get all lessons", operationId = "lessonGetAll")
    public ResponseEntity<List<LessonDto>> getAll() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lesson by id", operationId = "lessonGetById")
    public ResponseEntity<LessonDto> getLesson(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter lessons (JPQL)", operationId = "lessonFilterJpql")
    public ResponseEntity<Page<LessonDto>> filterLessons(
        @RequestParam(required = false) Long courseId,
        @RequestParam(required = false) String courseTitle,
        @RequestParam(required = false) String title,
        Pageable pageable) {
        return ResponseEntity.ok(lessonService.searchLessons(
            courseId, courseTitle, title, pageable, QueryMode.JPQL));
    }

    @GetMapping("/filter/native")
    @Operation(summary = "Filter lessons (native query)", operationId = "lessonFilterNative")
    public ResponseEntity<Page<LessonDto>> filterLessonsNative(
        @RequestParam(required = false) Long courseId,
        @RequestParam(required = false) String courseTitle,
        @RequestParam(required = false) String title,
        Pageable pageable) {
        return ResponseEntity.ok(lessonService.searchLessons(
            courseId, courseTitle, title, pageable, QueryMode.NATIVE));
    }

    @PostMapping
    @Operation(summary = "Create lesson", operationId = "lessonCreate")
    public ResponseEntity<LessonDto> createLesson(@Valid @RequestBody LessonDto lessonDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(lessonService.createLesson(lessonDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update lesson", operationId = "lessonUpdate")
    public ResponseEntity<LessonDto> updateLesson(
        @PathVariable Long id, @Valid @RequestBody LessonDto lessonDetails) {
        return ResponseEntity.ok(lessonService.updateLesson(id, lessonDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete lesson", operationId = "lessonDelete")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
