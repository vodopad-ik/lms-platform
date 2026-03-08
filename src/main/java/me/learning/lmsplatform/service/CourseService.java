package me.learning.lmsplatform.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.dto.CoursePatchDto;
import me.learning.lmsplatform.mapper.CourseMapper;
import me.learning.lmsplatform.model.Course;
import me.learning.lmsplatform.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

    private static final String NOT_FOUND_MSG = "Course not found with id: ";

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::mapToDto)
                .toList();
    }

    public CourseDto getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(courseMapper::mapToDto)
                .orElseThrow(() -> new RuntimeException(NOT_FOUND_MSG + id));
    }

    public CourseDto getCourseByTitle(String title) {
        return courseRepository.findByTitle(title)
                .map(courseMapper::mapToDto)
                .orElseThrow(() -> new RuntimeException("Course not found with title: " + title));
    }

    public CourseDto createCourse(CourseDto courseDto) {
        Course course = courseMapper.mapToEntity(courseDto);
        return courseMapper.mapToDto(courseRepository.save(course));
    }

    // PUT: полная замена всех полей
    public CourseDto updateCourse(Long id, CourseDto courseDto) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(NOT_FOUND_MSG + id));
        existing.setTitle(courseDto.getTitle());
        existing.setDescription(courseDto.getDescription());
        return courseMapper.mapToDto(courseRepository.save(existing));
    }

    // PATCH: частичное обновление — меняем только переданные поля
    public CourseDto patchCourse(Long id, CoursePatchDto patchDto) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(NOT_FOUND_MSG + id));
        if (patchDto.getTitle() != null) {
            existing.setTitle(patchDto.getTitle());
        }
        if (patchDto.getDescription() != null) {
            existing.setDescription(patchDto.getDescription());
        }
        return courseMapper.mapToDto(courseRepository.save(existing));
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
