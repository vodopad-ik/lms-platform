package me.learning.lmsplatform.mapper;

import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.CategoryShortDto;
import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.dto.CourseShortDto;
import me.learning.lmsplatform.dto.TeacherShortDto;
import me.learning.lmsplatform.model.Course;
import me.learning.lmsplatform.repository.CourseProjection;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseMapper {

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
        .teacherId(course.getTeacher() != null ? course.getTeacher().getId() : null)
        .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
        .build();
  }

  public CourseDto mapToDto(CourseProjection projection) {
    if (projection == null) {
      return null;
    }
    return CourseDto.builder()
        .id(projection.getId())
        .title(projection.getTitle())
        .description(projection.getDescription())
        .price(projection.getPrice())
        .durationWeeks(projection.getDurationWeeks())
        .teacher(projection.getTeacherId() != null ? TeacherShortDto.builder()
            .id(projection.getTeacherId())
            .name(projection.getTeacherName())
            .build() : null)
        .category(projection.getCategoryId() != null ? CategoryShortDto.builder()
            .id(projection.getCategoryId())
            .name(projection.getCategoryName())
            .build() : null)
        .teacherId(projection.getTeacherId())
        .categoryId(projection.getCategoryId())
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
    if (course == null) {
      return null;
    }
    return CourseShortDto.builder()
        .id(course.getId())
        .title(course.getTitle())
        .build();
  }
}
