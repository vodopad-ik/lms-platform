package me.learning.lmsplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {
    private Long id;
    private String name;
    private String email;
    private String department;
    private Integer experienceYears;
    private java.util.List<CourseShortDto> courses;
}
