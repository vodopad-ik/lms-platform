package me.learning.lmsplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Lesson payload")
public class LessonDto {
    @Schema(description = "Lesson id", example = "1")
    private Long id;

    @NotBlank(message = "Lesson title cannot be blank")
    @Size(max = 255, message = "Lesson title cannot exceed 255 characters")
    @Schema(description = "Lesson title", example = "Introduction")
    private String title;

    @NotBlank(message = "Lesson content cannot be blank")
    @Size(max = 5000, message = "Lesson content cannot exceed 5000 characters")
    @Schema(description = "Lesson content", example = "In this lesson we cover basics.")
    private String content;

    @Positive(message = "Lesson duration must be positive")
    @Max(value = 525600, message = "Lesson duration cannot exceed 525600 minutes (364 weeks)")
    @Schema(description = "Lesson duration in minutes", example = "45")
    private Integer durationMinutes;

    @Size(max = 500, message = "Video URL cannot exceed 500 characters")
    @Schema(description = "Video URL", example = "https://example.com/video")
    private String videoUrl;

    @Valid
    @Schema(description = "Related course")
    private CourseShortDto course;
}
