package me.learning.lmsplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursePatchDto {

    private String title;
    private String description;
    private Double price;
    private Integer durationWeeks;
    private Long teacherId;
    private Long categoryId;
}
