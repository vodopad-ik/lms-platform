package me.learning.lmsplatform.mapper;

import java.util.Collections;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.StudentDto;
import me.learning.lmsplatform.model.Student;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final CourseMapper courseMapper;

    public StudentDto mapToDto(Student student) {
        if (student == null) {
            return null;
        }
        return StudentDto.builder()
                .id(student.getId())
                .name(student.getName())
                .email(student.getEmail())
                .enrollmentDate(student.getEnrollmentDate())
                .courses(student.getCourses() != null
                        ? student.getCourses().stream()
                                .map(courseMapper::mapToShortDto)
                                .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    public Student mapToEntity(StudentDto dto) {
        if (dto == null) {
            return null;
        }
        return Student.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .enrollmentDate(dto.getEnrollmentDate())
                .build();
    }
}
