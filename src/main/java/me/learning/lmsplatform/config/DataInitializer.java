package me.learning.lmsplatform.config;

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
    Course javaCourse = Course.builder()
        .title("Java for Beginners")
        .description("Learn Java from scratch")
        .build();

    Course springCourse = Course.builder()
        .title("Spring Boot Deep Dive")
        .description("Master Spring Boot application development")
        .build();

    courseRepository.save(javaCourse);
    courseRepository.save(springCourse);

    log.info("Sample data initialized!");
  }
}
