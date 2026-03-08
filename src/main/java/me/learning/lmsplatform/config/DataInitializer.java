package me.learning.lmsplatform.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.learning.lmsplatform.model.Category;
import me.learning.lmsplatform.model.Course;
import me.learning.lmsplatform.model.Lesson;
import me.learning.lmsplatform.model.Teacher;
import me.learning.lmsplatform.repository.CategoryRepository;
import me.learning.lmsplatform.repository.CourseRepository;
import me.learning.lmsplatform.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final CourseRepository courseRepository;
  private final TeacherRepository teacherRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public void run(String... args) {
    if (courseRepository.count() == 0) {
      Category devCategory = categoryRepository.save(
          Category.builder().name("Development").build());
      Category opsCategory = categoryRepository.save(
          Category.builder().name("DevOps").build());

      Teacher johnDoe = teacherRepository.save(Teacher.builder()
          .name("John Doe")
          .email("john.doe@example.com")
          .build());

      Course javaCourse = courseRepository.save(Course.builder()
          .title("Java for Beginners")
          .description("Learn Java from scratch")
          .teacher(johnDoe)
          .category(devCategory)
          .build());

      Course dockerCourse = courseRepository.save(Course.builder()
          .title("Docker Essentials")
          .description("Master containerization basics")
          .teacher(johnDoe)
          .category(opsCategory)
          .build());

      javaCourse.setLessons(List.of(
          Lesson.builder().title("Java Intro").content("Welcome to Java")
              .course(javaCourse).build()));
      dockerCourse.setLessons(List.of(
          Lesson.builder().title("Docker Intro").content("Welcome to Docker")
              .course(dockerCourse).build()));

      courseRepository.save(javaCourse);
      courseRepository.save(dockerCourse);

      log.info("Sample data initialized: Teachers, Categories, Courses, Lessons!");
    } else {
      log.info("Database already contains data, skipping initialization.");
    }
  }
}
