package me.learning.lmsplatform.mapper;

import java.util.Collections;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.TeacherDto;
import me.learning.lmsplatform.model.Teacher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherMapper {

    private final CourseMapper courseMapper;

    public TeacherDto mapToDto(Teacher teacher) {
        if (teacher == null) {
            return null;
        }
        return TeacherDto.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .email(teacher.getEmail())
                .department(teacher.getDepartment())
                .experienceYears(teacher.getExperienceYears())
                .courses(teacher.getCourses() != null
                        ? teacher.getCourses().stream()
                                .map(courseMapper::mapToShortDto)
                                .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    public Teacher mapToEntity(TeacherDto dto) {
        if (dto == null) {
            return null;
        }
        return Teacher.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .experienceYears(dto.getExperienceYears())
                .build();
    }
}
