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
      // 1. Categories
      Category devCat = categoryRepository.save(Category.builder().name("Development").build());
      Category designCat = categoryRepository.save(Category.builder().name("Design").build());
      Category dataCat = categoryRepository.save(Category.builder().name("Data Science").build());
      Category devOpsCat = categoryRepository.save(Category.builder().name("DevOps").build());

      // 2. Teachers
      Teacher peters = teacherRepository.save(Teacher.builder()
          .name("Dr. Peters")
          .email("peters@university.edu")
          .build());
      Teacher smith = teacherRepository.save(Teacher.builder()
          .name("Alice Smith")
          .email("alice.smith@lms.com")
          .build());

      // 3. Courses
      Course java = courseRepository.save(Course.builder()
          .title("Java Masterclass")
          .description("Complete Java roadmap for enterprise")
          .teacher(peters)
          .category(devCat)
          .build());

      Course python = courseRepository.save(Course.builder()
          .title("Python for Data")
          .description("Analytics and ML with Python")
          .teacher(smith)
          .category(dataCat)
          .build());

      Course uiux = courseRepository.save(Course.builder()
          .title("UI/UX Basics")
          .description("User interface and experience design")
          .teacher(smith)
          .category(designCat)
          .build());

      Course docker = courseRepository.save(Course.builder()
          .title("Docker & K8s")
          .description("Cloud-native infrastructure guide")
          .teacher(peters)
          .category(devOpsCat)
          .build());

      Course react = courseRepository.save(Course.builder()
          .title("React.js Modern")
          .description("Hooks, Context API and Redux")
          .teacher(smith)
          .category(devCat)
          .build());

      // 4. Lessons for one course (Java)
      java.setLessons(List.of(
          Lesson.builder().title("Introduction").content("Intro to JVM").course(java).build(),
          Lesson.builder().title("Classes").content("OOP Principles").course(java).build(),
          Lesson.builder().title("Collections").content("Lists and Maps").course(java).build()
      ));

      courseRepository.save(java);

      log.info("Finished: Enhanced data seed with multiple courses and teachers.");
    }
  }
}
