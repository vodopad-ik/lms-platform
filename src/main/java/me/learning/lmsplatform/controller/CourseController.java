package me.learning.lmsplatform.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.dto.CoursePatchDto;
import me.learning.lmsplatform.service.CourseService;
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

  @GetMapping("/search")
  public ResponseEntity<CourseDto> getCourseByTitle(@RequestParam String title) {
    return ResponseEntity.ok(courseService.getCourseByTitle(title));
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
}
