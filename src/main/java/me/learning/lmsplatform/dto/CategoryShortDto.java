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
@Schema(description = "Compact category representation")
public class CategoryShortDto {
    @Positive(message = "Category id must be positive")
    @Schema(description = "Category id", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Development")
    private String name;
}
