package me.learning.lmsplatform.dto;

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
public class CourseDto {
  private Long id;
  
  @NotBlank(message = "Title cannot be blank")
  private String title;
  
  @NotBlank(message = "Description cannot be blank")
  private String description;
  
  @NotNull(message = "Price cannot be null")
  @Positive(message = "Price must be positive")
  private Double price;
  
  @NotNull(message = "Duration weeks cannot be null")
  @Positive(message = "Duration weeks must be positive")
  private Integer durationWeeks;
  
  private TeacherShortDto teacher;
  private CategoryShortDto category;
}
