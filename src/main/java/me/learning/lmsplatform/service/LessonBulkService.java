package me.learning.lmsplatform.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.CourseShortDto;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.exception.SimulatedFailureException;
import me.learning.lmsplatform.mapper.LessonMapper;
import me.learning.lmsplatform.model.Course;
import me.learning.lmsplatform.model.Lesson;
import me.learning.lmsplatform.repository.CourseRepository;
import me.learning.lmsplatform.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonBulkService {

  private static final String COURSE_NOT_FOUND_MSG = "Course not found with id: ";

  private final LessonRepository lessonRepository;
  private final CourseRepository courseRepository;
  private final LessonMapper lessonMapper;
  private final CacheInvalidationService cacheInvalidationService;

  public List<LessonDto> createBulkWithoutTransaction(List<LessonDto> lessons) {
    List<LessonDto> created = lessons.stream()
        .map(this::createOne)
        .toList();
    cacheInvalidationService.onLessonChanged();
    return created;
  }

  @Transactional
  public List<LessonDto> createBulkWithTransaction(List<LessonDto> lessons) {
    List<LessonDto> created = lessons.stream()
        .map(this::createOne)
        .toList();
    cacheInvalidationService.onLessonChanged();
    return created;
  }

  private LessonDto createOne(LessonDto lessonDto) {
    if ("FAIL".equalsIgnoreCase(lessonDto.getTitle())) {
      throw new SimulatedFailureException("Simulated failure for bulk operation");
    }

    Lesson lesson = lessonMapper.mapToEntity(lessonDto);

    Optional<Long> courseId = Optional.ofNullable(lessonDto.getCourse())
        .map(CourseShortDto::getId);

    Course course = courseId
        .map(id -> courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(COURSE_NOT_FOUND_MSG + id)))
        .orElse(null);

    lesson.setCourse(course);

    Lesson saved = lessonRepository.save(lesson);
    return lessonMapper.mapToDto(saved);
  }
}
