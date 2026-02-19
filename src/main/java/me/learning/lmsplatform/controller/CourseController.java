package me.learning.lmsplatform.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;

  @GetMapping("/{id}")
  public CourseDto getCourseById(@PathVariable Long id) {
    return courseService.getCourseById(id);
  }

  @GetMapping("/search")
  public CourseDto getCourseByTitle(@RequestParam String title) {
    return courseService.getCourseByTitle(title);
  }

  @GetMapping
  public List<CourseDto> getAllCourses() {
    return courseService.getAllCourses();
  }
}
