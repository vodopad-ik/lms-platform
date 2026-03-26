package me.learning.lmsplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Teacher payload")
public class TeacherDto {
    @Schema(description = "Teacher id", example = "1")
    private Long id;

    @NotBlank(message = "Teacher name cannot be blank")
    @Size(max = 255, message = "Teacher name cannot exceed 255 characters")
    @Schema(description = "Teacher full name", example = "Jane Smith")
    private String name;

    @NotBlank(message = "Teacher email cannot be blank")
    @Email(message = "Teacher email must be valid")
    @Size(max = 255, message = "Teacher email cannot exceed 255 characters")
    @Schema(description = "Teacher email", example = "jane@example.com")
    private String email;

    @NotBlank(message = "Department cannot be blank")
    @Size(max = 255, message = "Department cannot exceed 255 characters")
    @Schema(description = "Department", example = "Software Engineering")
    private String department;

    @NotNull(message = "Experience years cannot be null")
    @Min(value = 0, message = "Experience years cannot be negative")
    @Schema(description = "Experience in years", example = "5")
    private Integer experienceYears;

    @Valid
    @Schema(description = "Courses assigned to teacher")
    private List<CourseShortDto> courses;
}
