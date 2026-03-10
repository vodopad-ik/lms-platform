package me.learning.lmsplatform.mapper;

import me.learning.lmsplatform.dto.CourseShortDto;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.model.Lesson;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {
    public LessonDto mapToDto(Lesson lesson) {
        if (lesson == null) return null;
        return LessonDto.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .durationMinutes(lesson.getDurationMinutes())
                .videoUrl(lesson.getVideoUrl())
                .course(lesson.getCourse() != null ? CourseShortDto.builder()
                        .id(lesson.getCourse().getId())
                        .title(lesson.getCourse().getTitle())
                        .build() : null)
                .build();
    }

    public Lesson mapToEntity(LessonDto dto) {
        if (dto == null) return null;
        return Lesson.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .durationMinutes(dto.getDurationMinutes())
                .videoUrl(dto.getVideoUrl())
                .build();
    }
}
