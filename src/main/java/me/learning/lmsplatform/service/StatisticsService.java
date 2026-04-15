package me.learning.lmsplatform.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.repository.CategoryRepository;
import me.learning.lmsplatform.repository.CourseRepository;
import me.learning.lmsplatform.repository.LessonRepository;
import me.learning.lmsplatform.repository.StudentRepository;
import me.learning.lmsplatform.repository.TeacherRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

  private final CourseRepository courseRepository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final LessonRepository lessonRepository;
  private final CategoryRepository categoryRepository;

  public LmsStatistics generateStatistics() {
    long totalCourses = courseRepository.count();
    long totalStudents = studentRepository.count();
    long totalTeachers = teacherRepository.count();
    long totalLessons = lessonRepository.count();
    long totalCategories = categoryRepository.count();

    Double averagePrice = 0.0;
    if (totalCourses > 0) {
      averagePrice = courseRepository.findAll().stream()
          .mapToDouble(c -> c.getPrice().doubleValue())
          .average()
          .orElse(0.0);
    }

    return new LmsStatistics(
        totalCourses,
        totalStudents,
        totalTeachers,
        totalLessons,
        totalCategories,
        BigDecimal.valueOf(averagePrice).setScale(2, RoundingMode.HALF_UP)
    );
  }

  public record LmsStatistics(
      long totalCourses,
      long totalStudents,
      long totalTeachers,
      long totalLessons,
      long totalCategories,
      BigDecimal averageCoursePrice
  ) {
    @Override
    public String toString() {
      return String.format(
          "LMS Statistics: %d courses, %d students, %d teachers, %d lessons, "
              + "%d categories, avg price: $%s",
          totalCourses, totalStudents, totalTeachers, totalLessons,
          totalCategories, averageCoursePrice
      );
    }
  }
}
