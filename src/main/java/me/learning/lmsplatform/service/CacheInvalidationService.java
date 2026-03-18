package me.learning.lmsplatform.service;

import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.CategoryQueryCache;
import me.learning.lmsplatform.cache.CourseQueryCache;
import me.learning.lmsplatform.cache.LessonQueryCache;
import me.learning.lmsplatform.cache.StudentQueryCache;
import me.learning.lmsplatform.cache.TeacherQueryCache;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheInvalidationService {

  private final CourseQueryCache courseQueryCache;
  private final TeacherQueryCache teacherQueryCache;
  private final StudentQueryCache studentQueryCache;
  private final LessonQueryCache lessonQueryCache;
  private final CategoryQueryCache categoryQueryCache;

  public void onCourseChanged() {
    courseQueryCache.invalidateAll();
    teacherQueryCache.invalidateAll();
    studentQueryCache.invalidateAll();
    lessonQueryCache.invalidateAll();
    categoryQueryCache.invalidateAll();
  }

  public void onTeacherChanged() {
    teacherQueryCache.invalidateAll();
    courseQueryCache.invalidateAll();
    categoryQueryCache.invalidateAll();
  }

  public void onStudentChanged() {
    studentQueryCache.invalidateAll();
  }

  public void onLessonChanged() {
    lessonQueryCache.invalidateAll();
  }

  public void onCategoryChanged() {
    categoryQueryCache.invalidateAll();
    courseQueryCache.invalidateAll();
    teacherQueryCache.invalidateAll();
  }
}

