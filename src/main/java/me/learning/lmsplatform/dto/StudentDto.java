package me.learning.lmsplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Student payload")
public class StudentDto {
    @Schema(description = "Student id", example = "1")
    private Long id;

    @NotBlank(message = "Student name cannot be blank")
    @Size(max = 255, message = "Student name cannot exceed 255 characters")
    @Schema(description = "Student full name", example = "John Doe")
    private String name;

    @NotBlank(message = "Student email cannot be blank")
    @Email(message = "Student email must be valid")
    @Size(max = 255, message = "Student email cannot exceed 255 characters")
    @Schema(description = "Student email", example = "john@example.com")
    private String email;

    @NotNull(message = "Enrollment date cannot be null")
    @Schema(description = "Enrollment date", example = "2026-03-01")
    private LocalDate enrollmentDate;

    @Valid
    @Schema(description = "Courses assigned to student")
    private List<CourseShortDto> courses;
}
