package me.learning.lmsplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Compact course representation")
public class CourseShortDto {
    @Positive(message = "Course id must be positive")
    @Schema(description = "Course id", example = "10")
    private Long id;

    @Schema(description = "Course title", example = "Java Masterclass")
    private String title;
}
