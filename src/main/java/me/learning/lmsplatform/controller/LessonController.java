package me.learning.lmsplatform.controller;

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
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<List<LessonDto>> getAll() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonDto> getLesson(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<LessonDto>> filterLessons(
        @RequestParam(required = false) Long courseId,
        @RequestParam(required = false) String courseTitle,
        @RequestParam(required = false) String title,
        Pageable pageable) {
        return ResponseEntity.ok(lessonService.searchLessons(
            courseId, courseTitle, title, pageable, QueryMode.JPQL));
    }

    @GetMapping("/filter/native")
    public ResponseEntity<Page<LessonDto>> filterLessonsNative(
        @RequestParam(required = false) Long courseId,
        @RequestParam(required = false) String courseTitle,
        @RequestParam(required = false) String title,
        Pageable pageable) {
        return ResponseEntity.ok(lessonService.searchLessons(
            courseId, courseTitle, title, pageable, QueryMode.NATIVE));
    }

    @PostMapping
    public ResponseEntity<LessonDto> createLesson(@RequestBody LessonDto lessonDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(lessonService.createLesson(lessonDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonDto> updateLesson(
        @PathVariable Long id, @RequestBody LessonDto lessonDetails) {
        return ResponseEntity.ok(lessonService.updateLesson(id, lessonDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
