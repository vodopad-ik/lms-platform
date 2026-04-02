package me.learning.lmsplatform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.dto.CourseShortDto;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.exception.SimulatedFailureException;
import me.learning.lmsplatform.mapper.LessonMapper;
import me.learning.lmsplatform.model.Course;
import me.learning.lmsplatform.model.Lesson;
import me.learning.lmsplatform.repository.CourseRepository;
import me.learning.lmsplatform.repository.LessonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LessonBulkServiceTest {

  @Mock
  private LessonRepository lessonRepository;

  @Mock
  private CourseRepository courseRepository;

  @Mock
  private LessonMapper lessonMapper;

  @Mock
  private CacheInvalidationService cacheInvalidationService;

  @InjectMocks
  private LessonBulkService lessonBulkService;

  @Test
  void createBulkWithoutTransaction_whenCourseIsNull_doesNotCallCourseRepository() {
    LessonDto dto = LessonDto.builder()
        .title("Lesson 1")
        .content("Content")
        .durationMinutes(10)
        .build();

    Lesson lessonEntity = new Lesson();
    when(lessonMapper.mapToEntity(dto)).thenReturn(lessonEntity);

    Lesson saved = new Lesson();
    saved.setId(1L);
    when(lessonRepository.save(lessonEntity)).thenReturn(saved);

    LessonDto mapped = LessonDto.builder().id(1L).title("Lesson 1").build();
    when(lessonMapper.mapToDto(saved)).thenReturn(mapped);

    List<LessonDto> result = lessonBulkService.createBulkWithoutTransaction(List.of(dto));

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).getId());
    verify(courseRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onLessonChanged();
  }

  @Test
  void createBulkWithTransaction_whenCourseIdProvided_setsCourseAndSaves() {
    LessonDto dto = LessonDto.builder()
        .title("Lesson 1")
        .content("Content")
        .durationMinutes(10)
        .course(CourseShortDto.builder().id(5L).build())
        .build();

    Lesson lessonEntity = new Lesson();
    when(lessonMapper.mapToEntity(dto)).thenReturn(lessonEntity);

    Course course = new Course();
    course.setId(5L);
    when(courseRepository.findById(5L)).thenReturn(Optional.of(course));

    Lesson saved = new Lesson();
    saved.setId(10L);
    when(lessonRepository.save(lessonEntity)).thenReturn(saved);

    LessonDto mapped = LessonDto.builder().id(10L).title("Lesson 1").build();
    when(lessonMapper.mapToDto(saved)).thenReturn(mapped);

    List<LessonDto> result = lessonBulkService.createBulkWithTransaction(List.of(dto));

    assertEquals(1, result.size());
    assertEquals(10L, result.get(0).getId());
    assertNotNull(lessonEntity.getCourse());
    assertEquals(5L, lessonEntity.getCourse().getId());
    verify(courseRepository, times(1)).findById(5L);
    verify(cacheInvalidationService, times(1)).onLessonChanged();
  }

  @Test
  void createBulkWithoutTransaction_whenCourseNotFound_throwsNotFound() {
    LessonDto dto = LessonDto.builder()
        .title("Lesson 1")
        .content("Content")
        .durationMinutes(10)
        .course(CourseShortDto.builder().id(999L).build())
        .build();

    Lesson lessonEntity = new Lesson();
    when(lessonMapper.mapToEntity(dto)).thenReturn(lessonEntity);
    when(courseRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> {
          lessonBulkService.createBulkWithoutTransaction(List.of(dto));
        });

    verify(lessonRepository, never()).save(any());
    verify(cacheInvalidationService, never()).onLessonChanged();
  }

  @Test
  void createBulkWithoutTransaction_whenFailTitle_throwsAndStopsProcessing() {
    LessonDto ok = LessonDto.builder()
        .title("Lesson 1")
        .content("Content")
        .durationMinutes(10)
        .build();

    LessonDto fail = LessonDto.builder()
        .title("FAIL")
        .content("Content")
        .durationMinutes(10)
        .build();

    Lesson okEntity = new Lesson();
    when(lessonMapper.mapToEntity(ok)).thenReturn(okEntity);

    Lesson saved = new Lesson();
    saved.setId(1L);
    when(lessonRepository.save(okEntity)).thenReturn(saved);

    LessonDto mapped = LessonDto.builder().id(1L).title("Lesson 1").build();
    when(lessonMapper.mapToDto(saved)).thenReturn(mapped);

    assertThrows(SimulatedFailureException.class,
        () -> {
          lessonBulkService.createBulkWithoutTransaction(List.of(ok, fail));
        });

    verify(lessonRepository, times(1)).save(any(Lesson.class));
    verify(cacheInvalidationService, never()).onLessonChanged();
  }
}
