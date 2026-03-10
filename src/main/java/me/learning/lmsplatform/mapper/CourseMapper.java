package me.learning.lmsplatform.mapper;

import java.util.Collections;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.*;
import me.learning.lmsplatform.model.Course;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseMapper {

  private final LessonMapper lessonMapper;

  public CourseDto mapToDto(Course course) {
    if (course == null) {
      return null;
    }
    return CourseDto.builder()
        .id(course.getId())
        .title(course.getTitle())
        .description(course.getDescription())
        .price(course.getPrice())
        .durationWeeks(course.getDurationWeeks())
        .teacher(course.getTeacher() != null ? TeacherShortDto.builder()
                .id(course.getTeacher().getId())
                .name(course.getTeacher().getName())
                .build() : null)
        .category(course.getCategory() != null ? CategoryShortDto.builder()
                .id(course.getCategory().getId())
                .name(course.getCategory().getName())
                .build() : null)
        .lessons(course.getLessons() != null 
            ? course.getLessons().stream().map(lessonMapper::mapToDto).collect(Collectors.toList())
            : Collections.emptyList())
        .build();
  }

  public Course mapToEntity(CourseDto courseDto) {
    if (courseDto == null) {
      return null;
    }
    return Course.builder()
        .id(courseDto.getId())
        .title(courseDto.getTitle())
        .description(courseDto.getDescription())
        .price(courseDto.getPrice())
        .durationWeeks(courseDto.getDurationWeeks())
        .build();
  }

  public CourseShortDto mapToShortDto(Course course) {
    if (course == null) return null;
    return CourseShortDto.builder()
        .id(course.getId())
        .title(course.getTitle())
        .build();
  }
}
