package me.learning.lmsplatform.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.learning.lmsplatform.model.Course;
import me.learning.lmsplatform.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final CourseRepository courseRepository;

  @Override
  public void run(String... args) {
    if (courseRepository.count() == 0) {
      Course javaCourse = Course.builder()
          .title("Java for Beginners")
          .description("Learn Java from scratch")
          .build();

      Course springCourse = Course.builder()
          .title("Spring Boot Deep Dive")
          .description("Master Spring Boot application development")
          .build();

      Course dockerCourse = Course.builder()
          .title("Docker for Developers")
          .description("Containerize your applications easily")
          .build();

      Course sqlCourse = Course.builder()
          .title("PostgreSQL Advanced")
          .description("Master complex queries and optimization")
          .build();

      courseRepository.save(javaCourse);
      courseRepository.save(springCourse);
      courseRepository.save(dockerCourse);
      courseRepository.save(sqlCourse);

      log.info("Sample data initialized with 4 courses!");
    } else {
      log.info("Database already contains data, skipping initialization.");
    }
  }
}
