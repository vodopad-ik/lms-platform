package me.learning.lmsplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Partial course update payload")
public class CoursePatchDto {

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Schema(description = "Course title", example = "Advanced Java")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Schema(description = "Course description", example = "Updated course description")
    private String description;

    @Positive(message = "Price must be positive")
    @Schema(description = "Course price", example = "149.99")
    private Double price;

    @Positive(message = "Duration weeks must be positive")
    @Schema(description = "Course duration in weeks", example = "6")
    private Integer durationWeeks;

    @Positive(message = "Teacher id must be positive")
    @Schema(description = "Teacher id", example = "2")
    private Long teacherId;

    @Positive(message = "Category id must be positive")
    @Schema(description = "Category id", example = "3")
    private Long categoryId;
}
