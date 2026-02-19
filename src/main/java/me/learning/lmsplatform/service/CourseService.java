package me.learning.lmsplatform.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.model.Course;
import me.learning.lmsplatform.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository courseRepository;

  public CourseDto getCourseById(Long id) {
    return courseRepository.findById(id)
        .map(this::mapToDto)
        .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
  }

  public CourseDto getCourseByTitle(String title) {
    return courseRepository.findByTitle(title)
        .map(this::mapToDto)
        .orElseThrow(() -> new RuntimeException("Course not found with title: " + title));
  }

  public List<CourseDto> getAllCourses() {
    return courseRepository.findAll().stream()
        .map(this::mapToDto)
        .toList();
  }

  private CourseDto mapToDto(Course course) {
    return CourseDto.builder()
        .id(course.getId())
        .title(course.getTitle())
        .description(course.getDescription())
        .build();
  }
}
