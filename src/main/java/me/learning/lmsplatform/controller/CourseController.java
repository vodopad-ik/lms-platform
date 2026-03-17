package me.learning.lmsplatform.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.dto.CoursePatchDto;
import me.learning.lmsplatform.dto.LessonCreateDto;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;

  @GetMapping
  public ResponseEntity<List<CourseDto>> getAllCourses() {
    return ResponseEntity.ok(courseService.getAllCourses());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
    return ResponseEntity.ok(courseService.getCourseById(id));
  }

  @GetMapping("/{courseId}/lessons")
  public ResponseEntity<List<LessonDto>> getCourseLessons(@PathVariable Long courseId) {
    return ResponseEntity.ok(courseService.getLessonsByCourseId(courseId));
  }

  @PostMapping("/{courseId}/lessons")
  public ResponseEntity<LessonDto> addLessonToCourse(
      @PathVariable Long courseId, @RequestBody LessonCreateDto lessonDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(courseService.addLessonToCourse(courseId, lessonDto));
  }

  @GetMapping("/search")
  public ResponseEntity<CourseDto> getCourseByTitle(@RequestParam String title) {
    return ResponseEntity.ok(courseService.getCourseByTitle(title));
  }

  @GetMapping("/filter")
  public ResponseEntity<Page<CourseDto>> filterCourses(
      @RequestParam(required = false) String department,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      Pageable pageable) {
    return ResponseEntity.ok(courseService.searchCourses(
        department, category, minPrice, maxPrice, pageable, QueryMode.JPQL));
  }

  @GetMapping("/filter/native")
  public ResponseEntity<Page<CourseDto>> filterCoursesNative(
      @RequestParam(required = false) String department,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      Pageable pageable) {
    return ResponseEntity.ok(courseService.searchCourses(
        department, category, minPrice, maxPrice, pageable, QueryMode.NATIVE));
  }

  @PostMapping
  public ResponseEntity<CourseDto> createCourse(@RequestBody CourseDto courseDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(courseDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CourseDto> updateCourse(
      @PathVariable Long id, @RequestBody CourseDto courseDto) {
    return ResponseEntity.ok(courseService.updateCourse(id, courseDto));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<CourseDto> patchCourse(
      @PathVariable Long id, @RequestBody CoursePatchDto patchDto) {
    return ResponseEntity.ok(courseService.patchCourse(id, patchDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{courseId}/students/{studentId}")
  public ResponseEntity<CourseDto> addStudentToCourse(
      @PathVariable Long courseId, @PathVariable Long studentId) {
    return ResponseEntity.ok(courseService.addStudentToCourse(courseId, studentId));
  }
}
