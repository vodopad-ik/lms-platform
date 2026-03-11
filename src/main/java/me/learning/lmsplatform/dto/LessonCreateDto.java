package me.learning.lmsplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCreateDto {
    private String title;
    private String content;
    private Integer durationMinutes;
    private String videoUrl;
}
