package me.learning.lmsplatform.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.LessonQueryCache;
import me.learning.lmsplatform.cache.LessonQueryCacheKey;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.mapper.LessonMapper;
import me.learning.lmsplatform.model.Lesson;
import me.learning.lmsplatform.repository.CourseRepository;
import me.learning.lmsplatform.repository.LessonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonService {

  private static final String NOT_FOUND_MSG = "Lesson not found with id: ";
  private static final String COURSE_NOT_FOUND_MSG = "Course not found with id: ";

  private final LessonRepository lessonRepository;
  private final CourseRepository courseRepository;
  private final LessonMapper lessonMapper;
  private final LessonQueryCache lessonQueryCache;
  private final CacheInvalidationService cacheInvalidationService;

  public List<LessonDto> getAllLessons() {
    return lessonRepository.findAll().stream()
        .map(lessonMapper::mapToDto)
        .toList();
  }

  public LessonDto getLessonById(Long id) {
    return lessonRepository.findById(id)
        .map(lessonMapper::mapToDto)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
  }

  public List<LessonDto> getLessonsByCourseId(Long courseId) {
    if (!courseRepository.existsById(courseId)) {
      throw new ResourceNotFoundException(COURSE_NOT_FOUND_MSG + courseId);
    }
    return lessonRepository.findByCourseId(courseId).stream()
        .map(lessonMapper::mapToDto)
        .toList();
  }

  public LessonDto createLesson(LessonDto lessonDto) {
    Lesson lesson = lessonMapper.mapToEntity(lessonDto);
    if (lessonDto.getCourseId() != null) {
      courseRepository.findById(lessonDto.getCourseId())
          .ifPresentOrElse(
              lesson::setCourse,
              () -> {
                throw new ResourceNotFoundException(
                    COURSE_NOT_FOUND_MSG + lessonDto.getCourseId());
              });
    }
    Lesson saved = lessonRepository.save(lesson);
    cacheInvalidationService.onLessonChanged();
    return lessonMapper.mapToDto(saved);
  }

  public LessonDto updateLesson(Long id, LessonDto lessonDto) {
    Lesson existing = lessonRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
    existing.setTitle(lessonDto.getTitle());
    existing.setContent(lessonDto.getContent());
    existing.setDurationMinutes(lessonDto.getDurationMinutes());
    if (lessonDto.getCourseId() != null) {
      courseRepository.findById(lessonDto.getCourseId())
          .ifPresentOrElse(
              existing::setCourse,
              () -> {
                throw new ResourceNotFoundException(
                    COURSE_NOT_FOUND_MSG + lessonDto.getCourseId());
              });
    }
    Lesson saved = lessonRepository.save(existing);
    cacheInvalidationService.onLessonChanged();
    return lessonMapper.mapToDto(saved);
  }

  public void deleteLesson(Long id) {
    lessonRepository.deleteById(id);
    cacheInvalidationService.onLessonChanged();
  }

  public Page<LessonDto> searchLessons(
      Long courseId,
      String courseTitle,
      String titleFilter,
      Pageable pageable,
      QueryMode queryMode) {
    String normalizedCourseTitle = normalizeLike(courseTitle);
    String normalizedTitleFilter = normalizeLike(titleFilter);
    LessonQueryCacheKey key = LessonQueryCacheKey.from(
        queryMode, courseId, normalizedCourseTitle, normalizedTitleFilter, pageable);
    return lessonQueryCache.getOrLoad(
        key,
        () -> fetchLessonsWithFilters(
            courseId, normalizedCourseTitle, normalizedTitleFilter, pageable, queryMode));
  }

  private Page<LessonDto> fetchLessonsWithFilters(
      Long courseId,
      String courseTitle,
      String titleFilter,
      Pageable pageable,
      QueryMode queryMode) {
    if (queryMode == QueryMode.NATIVE) {
      Page<Lesson> nativePage = lessonRepository.findWithFiltersNative(
          courseId, courseTitle, titleFilter, pageable);
      return mapLessonPage(nativePage, pageable);
    }
    return lessonRepository.findWithFilters(courseId, courseTitle, titleFilter, pageable)
        .map(lessonMapper::mapToDto);
  }

  private Page<LessonDto> mapLessonPage(Page<Lesson> lessonPage, Pageable pageable) {
    return new PageImpl<>(
        lessonPage.stream()
            .map(lessonMapper::mapToDto)
            .toList(),
        pageable,
        lessonPage.getTotalElements());
  }

  private static String normalizeLike(String value) {
    return value == null ? "" : value;
  }
}
