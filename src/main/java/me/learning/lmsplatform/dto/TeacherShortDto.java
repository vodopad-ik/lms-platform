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
@Schema(description = "Compact teacher representation")
public class TeacherShortDto {
    @Positive(message = "Teacher id must be positive")
    @Schema(description = "Teacher id", example = "5")
    private Long id;

    @Schema(description = "Teacher name", example = "Jane Smith")
    private String name;
}
