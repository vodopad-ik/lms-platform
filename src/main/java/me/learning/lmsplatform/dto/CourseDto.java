package me.learning.lmsplatform.dto;

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
  private String title;
  private String description;
  private Double price;
  private Integer durationWeeks;
  private TeacherShortDto teacher;
  private CategoryShortDto category;
  private Long teacherId;
  private Long categoryId;
}
