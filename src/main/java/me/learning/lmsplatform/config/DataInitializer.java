package me.learning.lmsplatform.config;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.learning.lmsplatform.model.Category;
import me.learning.lmsplatform.model.Course;
import me.learning.lmsplatform.model.Lesson;
import me.learning.lmsplatform.model.Student;
import me.learning.lmsplatform.model.Teacher;
import me.learning.lmsplatform.repository.CategoryRepository;
import me.learning.lmsplatform.repository.CourseRepository;
import me.learning.lmsplatform.repository.StudentRepository;
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
    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            // 1. Categories
            Category devCat = categoryRepository.save(
                Category.builder().name("Development").build());
            Category designCat = categoryRepository.save(
                Category.builder().name("Design").build());
            Category dataCat = categoryRepository.save(
                Category.builder().name("Data Science").build());
            Category devOpsCat = categoryRepository.save(
                Category.builder().name("DevOps").build());

            // 2. Teachers
            Teacher peters = teacherRepository.save(Teacher.builder()
                .name("Dr. Peters")
                .email("peters@university.edu")
                .build());
            Teacher smith = teacherRepository.save(Teacher.builder()
                .name("Alice Smith")
                .email("alice.smith@lms.com")
                .build());

            // 3. Students
            Student vlat = studentRepository.save(Student.builder().name("Vlat").build());
            Student marina = studentRepository.save(Student.builder().name("Marina").build());

            // 4. Courses
            Course java = courseRepository.save(Course.builder()
                .title("Java Masterclass")
                .description("Complete Java roadmap for enterprise")
                .teacher(peters)
                .category(devCat)
                .students(Set.of(vlat, marina))
                .build());

            Course python = courseRepository.save(Course.builder()
                .title("Python for Data")
                .description("Analytics and ML with Python")
                .teacher(smith)
                .category(dataCat)
                .students(Set.of(vlat))
                .build());

            Course uiux = courseRepository.save(Course.builder()
                .title("UI/UX Basics")
                .description("User interface and experience design")
                .teacher(smith)
                .category(designCat)
                .students(Set.of(marina))
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

            // 5. Lessons for one course (Java)
            java.setLessons(List.of(
                Lesson.builder().title("Introduction").content("Intro to JVM")
                    .course(java).build(),
                Lesson.builder().title("Classes").content("OOP Principles")
                    .course(java).build(),
                Lesson.builder().title("Collections").content("Lists and Maps")
                    .course(java).build()
            ));

            courseRepository.save(java);

            log.info("Finished: Full data seed with Courses, Teachers, Categories, Students"
                 + " and Lessons.");
        }
    }
}
