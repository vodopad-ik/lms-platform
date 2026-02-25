package me.learning.lmsplatform.mapper;

import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseDto mapToDto(Course course) {
        if (course == null) {
            return null;
        }
        return CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
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
                .build();
    }
}
