package me.learning.lmsplatform.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.mapper.LessonMapper;
import me.learning.lmsplatform.repository.LessonRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    @GetMapping
    public List<LessonDto> getAll() {
        return lessonRepository.findAll().stream()
                .map(lessonMapper::mapToDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    public void deleteLesson(@PathVariable Long id) {
        if (id != null) {
            lessonRepository.deleteById(id);
        }
    }

    @GetMapping("/{id}")
    public LessonDto getLesson(@PathVariable Long id) {
        if (id == null) return null;
        return lessonRepository.findById(id)
                .map(lessonMapper::mapToDto)
                .orElse(null);
    }

    @PostMapping
    public LessonDto createLesson(@RequestBody LessonDto lessonDto) {
        if (lessonDto == null) return null;
        return lessonMapper.mapToDto(lessonRepository.save(lessonMapper.mapToEntity(lessonDto)));
    }

    @PutMapping("/{id}")
    public LessonDto updateLesson(@PathVariable Long id, @RequestBody LessonDto lessonDetails) {
        if (id == null || lessonDetails == null) return null;
        return lessonRepository.findById(id)
                .map(lesson -> {
                    lesson.setTitle(lessonDetails.getTitle());
                    lesson.setContent(lessonDetails.getContent());
                    lesson.setDurationMinutes(lessonDetails.getDurationMinutes());
                    lesson.setVideoUrl(lessonDetails.getVideoUrl());
                    return lessonMapper.mapToDto(lessonRepository.save(lesson));
                })
                .orElse(null);
    }
}
