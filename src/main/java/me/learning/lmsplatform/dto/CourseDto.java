package me.learning.lmsplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Course payload")
public class CourseDto {
  @Schema(description = "Course id", example = "1")
  private Long id;
  
  @NotBlank(message = "Title cannot be blank")
  @Schema(description = "Course title", example = "Java Masterclass")
  private String title;
  
  @NotBlank(message = "Description cannot be blank")
  @Schema(description = "Course description", example = "Complete Java course")
  private String description;
  
  @NotNull(message = "Price cannot be null")
  @Positive(message = "Price must be positive")
  @Schema(description = "Course price", example = "199.99")
  private Double price;
  
  @NotNull(message = "Duration weeks cannot be null")
  @Positive(message = "Duration weeks must be positive")
  @Max(value = 52, message = "Duration weeks cannot exceed 52 weeks (approximately 1 year)")
  @Schema(description = "Course duration in weeks", example = "8")
  private Integer durationWeeks;
  
  @Valid
  @Schema(description = "Assigned teacher")
  private TeacherShortDto teacher;
  @Valid
  @Schema(description = "Assigned category")
  private CategoryShortDto category;
}
