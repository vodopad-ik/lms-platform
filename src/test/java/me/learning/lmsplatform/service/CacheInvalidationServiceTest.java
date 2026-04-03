package me.learning.lmsplatform.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import me.learning.lmsplatform.cache.CategoryQueryCache;
import me.learning.lmsplatform.cache.CourseQueryCache;
import me.learning.lmsplatform.cache.LessonQueryCache;
import me.learning.lmsplatform.cache.StudentQueryCache;
import me.learning.lmsplatform.cache.TeacherQueryCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CacheInvalidationServiceTest {

  @Mock
  private CourseQueryCache courseQueryCache;

  @Mock
  private TeacherQueryCache teacherQueryCache;

  @Mock
  private StudentQueryCache studentQueryCache;

  @Mock
  private LessonQueryCache lessonQueryCache;

  @Mock
  private CategoryQueryCache categoryQueryCache;

  @InjectMocks
  private CacheInvalidationService cacheInvalidationService;

  @Test
  void onCourseChanged_shouldInvalidateAllCaches() {
    cacheInvalidationService.onCourseChanged();

    verify(courseQueryCache, times(1)).invalidateAll();
    verify(teacherQueryCache, times(1)).invalidateAll();
    verify(studentQueryCache, times(1)).invalidateAll();
    verify(lessonQueryCache, times(1)).invalidateAll();
    verify(categoryQueryCache, times(1)).invalidateAll();
  }

  @Test
  void onTeacherChanged_shouldInvalidateRelevantCaches() {
    cacheInvalidationService.onTeacherChanged();

    verify(teacherQueryCache, times(1)).invalidateAll();
    verify(courseQueryCache, times(1)).invalidateAll();
    verify(categoryQueryCache, times(1)).invalidateAll();
  }

  @Test
  void onStudentChanged_shouldInvalidateStudentCache() {
    cacheInvalidationService.onStudentChanged();

    verify(studentQueryCache, times(1)).invalidateAll();
  }

  @Test
  void onLessonChanged_shouldInvalidateLessonCache() {
    cacheInvalidationService.onLessonChanged();

    verify(lessonQueryCache, times(1)).invalidateAll();
  }

  @Test
  void onCategoryChanged_shouldInvalidateRelevantCaches() {
    cacheInvalidationService.onCategoryChanged();

    verify(categoryQueryCache, times(1)).invalidateAll();
    verify(courseQueryCache, times(1)).invalidateAll();
    verify(teacherQueryCache, times(1)).invalidateAll();
  }
}
