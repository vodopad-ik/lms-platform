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
import me.learning.lmsplatform.cache.LessonQueryCache;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.CourseShortDto;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

  @Mock
  private LessonRepository lessonRepository;

  @Mock
  private CourseRepository courseRepository;

  @Mock
  private LessonMapper lessonMapper;

  @Mock
  private LessonQueryCache lessonQueryCache;

  @Mock
  private CacheInvalidationService cacheInvalidationService;

  @InjectMocks
  private LessonService lessonService;

  @Test
  void getAllLessons_shouldReturnAllLessons() {
    Lesson lesson1 = new Lesson();
    lesson1.setId(1L);
    lesson1.setTitle("Lesson 1");

    Lesson lesson2 = new Lesson();
    lesson2.setId(2L);
    lesson2.setTitle("Lesson 2");

    when(lessonRepository.findAll()).thenReturn(List.of(lesson1, lesson2));
    when(lessonMapper.mapToDto(lesson1)).thenReturn(LessonDto.builder().id(1L).title("Lesson 1").build());
    when(lessonMapper.mapToDto(lesson2)).thenReturn(LessonDto.builder().id(2L).title("Lesson 2").build());

    List<LessonDto> result = lessonService.getAllLessons();

    assertEquals(2, result.size());
  }

  @Test
  void getLessonById_whenExists_shouldReturnLesson() {
    Lesson lesson = new Lesson();
    lesson.setId(1L);
    lesson.setTitle("Test Lesson");

    when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
    when(lessonMapper.mapToDto(lesson)).thenReturn(LessonDto.builder().id(1L).title("Test Lesson").build());

    LessonDto result = lessonService.getLessonById(1L);

    assertNotNull(result);
    assertEquals("Test Lesson", result.getTitle());
  }

  @Test
  void getLessonById_whenNotExists_shouldThrowException() {
    when(lessonRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> lessonService.getLessonById(999L));
  }

  @Test
  void getLessonsByCourseId_whenCourseExists_shouldReturnLessons() {
    Lesson lesson = new Lesson();
    lesson.setId(1L);
    lesson.setTitle("Lesson");

    when(courseRepository.existsById(1L)).thenReturn(true);
    when(lessonRepository.findByCourseId(1L)).thenReturn(List.of(lesson));
    when(lessonMapper.mapToDto(lesson)).thenReturn(LessonDto.builder().id(1L).title("Lesson").build());

    List<LessonDto> result = lessonService.getLessonsByCourseId(1L);

    assertEquals(1, result.size());
  }

  @Test
  void getLessonsByCourseId_whenCourseNotExists_shouldThrowException() {
    when(courseRepository.existsById(999L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> lessonService.getLessonsByCourseId(999L));
    verify(lessonRepository, never()).findByCourseId(any());
  }

  @Test
  void createLesson_withCourse_shouldSaveAndReturnLesson() {
    Course course = new Course();
    course.setId(1L);

    LessonDto dto = LessonDto.builder()
        .title("New Lesson")
        .course(CourseShortDto.builder().id(1L).build())
        .build();
    Lesson entity = new Lesson();
    entity.setTitle("New Lesson");
    Lesson saved = new Lesson();
    saved.setId(1L);
    saved.setTitle("New Lesson");
    saved.setCourse(course);

    when(lessonMapper.mapToEntity(dto)).thenReturn(entity);
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
    when(lessonRepository.save(entity)).thenReturn(saved);
    when(lessonMapper.mapToDto(saved)).thenReturn(LessonDto.builder().id(1L).title("New Lesson").build());

    LessonDto result = lessonService.createLesson(dto);

    assertNotNull(result.getId());
    verify(cacheInvalidationService, times(1)).onLessonChanged();
  }

  @Test
  void createLesson_whenCourseNotFound_shouldThrowException() {
    LessonDto dto = LessonDto.builder()
        .title("New Lesson")
        .course(CourseShortDto.builder().id(999L).build())
        .build();
    Lesson entity = new Lesson();

    when(lessonMapper.mapToEntity(dto)).thenReturn(entity);
    when(courseRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> lessonService.createLesson(dto));
  }

  @Test
  void createLesson_withoutCourse_shouldSaveWithoutCourse() {
    LessonDto dto = LessonDto.builder().title("New Lesson").build();
    Lesson entity = new Lesson();
    entity.setTitle("New Lesson");
    Lesson saved = new Lesson();
    saved.setId(1L);
    saved.setTitle("New Lesson");

    when(lessonMapper.mapToEntity(dto)).thenReturn(entity);
    when(lessonRepository.save(entity)).thenReturn(saved);
    when(lessonMapper.mapToDto(saved)).thenReturn(LessonDto.builder().id(1L).title("New Lesson").build());

    LessonDto result = lessonService.createLesson(dto);

    assertNotNull(result.getId());
    verify(courseRepository, never()).findById(any());
  }

  @Test
  void updateLesson_whenExists_shouldUpdateAndReturnLesson() {
    Course course = new Course();
    course.setId(1L);

    LessonDto dto = LessonDto.builder()
        .title("Updated")
        .course(CourseShortDto.builder().id(1L).build())
        .build();
    Lesson existing = new Lesson();
    existing.setId(1L);
    existing.setTitle("Old");
    Lesson saved = new Lesson();
    saved.setId(1L);
    saved.setTitle("Updated");

    when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
    when(lessonRepository.save(existing)).thenReturn(saved);
    when(lessonMapper.mapToDto(saved)).thenReturn(LessonDto.builder().id(1L).title("Updated").build());

    LessonDto result = lessonService.updateLesson(1L, dto);

    assertEquals("Updated", result.getTitle());
    verify(cacheInvalidationService, times(1)).onLessonChanged();
  }

  @Test
  void updateLesson_whenNotExists_shouldThrowException() {
    LessonDto dto = LessonDto.builder().title("Updated").build();
    when(lessonRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> lessonService.updateLesson(999L, dto));
  }

  @Test
  void deleteLesson_shouldDeleteAndInvalidateCache() {
    lessonService.deleteLesson(1L);

    verify(lessonRepository, times(1)).deleteById(1L);
    verify(cacheInvalidationService, times(1)).onLessonChanged();
  }

  @Test
  void searchLessons_withJpqlMode_shouldReturnResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Lesson lesson = new Lesson();
    lesson.setId(1L);
    lesson.setTitle("Test");
    Page<Lesson> page = new PageImpl<>(List.of(lesson), pageable, 1);

    when(lessonQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(lessonRepository.findWithFilters(1L, "course", "title", pageable)).thenReturn(page);
    when(lessonMapper.mapToDto(lesson)).thenReturn(LessonDto.builder().id(1L).title("Test").build());

    Page<LessonDto> result = lessonService.searchLessons(1L, "course", "title", pageable, QueryMode.JPQL);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void updateLesson_withoutCourse_shouldSaveWithoutCourse() {
    LessonDto dto = LessonDto.builder().title("Updated Lesson").build();
    Lesson existing = new Lesson();
    existing.setId(1L);
    existing.setTitle("Old");
    Lesson saved = new Lesson();
    saved.setId(1L);
    saved.setTitle("Updated Lesson");

    when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(lessonRepository.save(existing)).thenReturn(saved);
    when(lessonMapper.mapToDto(saved)).thenReturn(LessonDto.builder().id(1L).title("Updated Lesson").build());

    LessonDto result = lessonService.updateLesson(1L, dto);

    assertEquals("Updated Lesson", result.getTitle());
    verify(courseRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onLessonChanged();
  }

  @Test
  void updateLesson_whenCourseNotFound_shouldThrowException() {
    LessonDto dto = LessonDto.builder()
        .title("Updated Lesson")
        .course(CourseShortDto.builder().id(999L).build())
        .build();
    Lesson existing = new Lesson();
    existing.setId(1L);
    existing.setTitle("Old");

    when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> lessonService.updateLesson(1L, dto));
  }

  @Test
  void searchLessons_withNullFilters_shouldNormalizeToEmptyString() {
    Pageable pageable = PageRequest.of(0, 10);
    Lesson lesson = new Lesson();
    lesson.setId(1L);
    lesson.setTitle("Test");
    Page<Lesson> page = new PageImpl<>(List.of(lesson), pageable, 1);

    when(lessonQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(lessonRepository.findWithFilters(1L, "", "", pageable)).thenReturn(page);
    when(lessonMapper.mapToDto(lesson)).thenReturn(LessonDto.builder().id(1L).title("Test").build());

    Page<LessonDto> result = lessonService.searchLessons(1L, null, null, pageable, QueryMode.JPQL);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void searchLessons_withNativeModeNullFilters_shouldNormalizeToEmptyString() {
    Pageable pageable = PageRequest.of(0, 10);
    Lesson lesson = new Lesson();
    lesson.setId(1L);
    lesson.setTitle("Test");
    Page<Lesson> page = new PageImpl<>(List.of(lesson), pageable, 1);

    when(lessonQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(lessonRepository.findWithFiltersNative(1L, "", "", pageable)).thenReturn(page);
    when(lessonMapper.mapToDto(lesson)).thenReturn(LessonDto.builder().id(1L).title("Test").build());

    Page<LessonDto> result = lessonService.searchLessons(1L, null, null, pageable, QueryMode.NATIVE);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void createLesson_withOnlyNullCourse_shouldSaveWithoutCourse() {
    LessonDto dto = LessonDto.builder()
        .title("New Lesson")
        .build();
    Lesson entity = new Lesson();
    entity.setTitle("New Lesson");
    Lesson saved = new Lesson();
    saved.setId(1L);
    saved.setTitle("New Lesson");

    when(lessonMapper.mapToEntity(dto)).thenReturn(entity);
    when(lessonRepository.save(entity)).thenReturn(saved);
    when(lessonMapper.mapToDto(saved)).thenReturn(LessonDto.builder().id(1L).title("New Lesson").build());

    LessonDto result = lessonService.createLesson(dto);

    assertNotNull(result.getId());
    verify(courseRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onLessonChanged();
  }

  @Test
  void updateLesson_withOnlyNullCourse_shouldSaveWithoutCourse() {
    LessonDto dto = LessonDto.builder()
        .title("Updated Lesson")
        .build();
    Lesson existing = new Lesson();
    existing.setId(1L);
    existing.setTitle("Old");
    Lesson saved = new Lesson();
    saved.setId(1L);
    saved.setTitle("Updated Lesson");

    when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(lessonRepository.save(existing)).thenReturn(saved);
    when(lessonMapper.mapToDto(saved)).thenReturn(LessonDto.builder().id(1L).title("Updated Lesson").build());

    LessonDto result = lessonService.updateLesson(1L, dto);

    assertEquals("Updated Lesson", result.getTitle());
    verify(courseRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onLessonChanged();
  }

  @Test
  void createLesson_withCourseNullId_shouldNotCallCourseRepository() {
    CourseShortDto nullIdCourse = new CourseShortDto();
    nullIdCourse.setId(null);

    LessonDto dto = LessonDto.builder()
        .title("New Lesson")
        .course(nullIdCourse)
        .build();
    Lesson entity = new Lesson();
    entity.setTitle("New Lesson");
    Lesson saved = new Lesson();
    saved.setId(1L);
    saved.setTitle("New Lesson");

    when(lessonMapper.mapToEntity(dto)).thenReturn(entity);
    when(lessonRepository.save(entity)).thenReturn(saved);
    when(lessonMapper.mapToDto(saved)).thenReturn(LessonDto.builder().id(1L).title("New Lesson").build());

    LessonDto result = lessonService.createLesson(dto);

    assertNotNull(result.getId());
    verify(courseRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onLessonChanged();
  }
}
