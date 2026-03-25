package me.learning.lmsplatform.config;

import java.time.LocalDate;
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
            Category devCat = categoryRepository.save(
                Category.builder().name("Development").build());
            Category designCat = categoryRepository.save(
                Category.builder().name("Design").build());
            Category dataCat = categoryRepository.save(
                Category.builder().name("Data Science").build());
            Category devOpsCat = categoryRepository.save(
                Category.builder().name("DevOps").build());

            Teacher peters = teacherRepository.save(Teacher.builder()
                .name("Dr. Peters")
                .email("peters@university.edu")
                .department("Computer Science")
                .experienceYears(15)
                .build());
            Teacher smith = teacherRepository.save(Teacher.builder()
                .name("Alice Smith")
                .email("alice.smith@lms.com")
                .department("Software Engineering")
                .experienceYears(8)
                .build());

            Student vlat = studentRepository.save(Student.builder()
                .name("Vlad")
                .email("vlad@example.com")
                .enrollmentDate(LocalDate.now().minusDays(10))
                .build());
            Student marina = studentRepository.save(Student.builder()
                .name("Marina")
                .email("marina@example.com")
                .enrollmentDate(LocalDate.now().minusDays(5))
                .build());

            courseRepository.save(Course.builder()  
                .title("Python for Data")
                .description("Analytics and ML with Python")
                .teacher(smith)
                .category(dataCat)
                .price(149.99)
                .durationWeeks(8)
                .students(Set.of(vlat))
                .build());

            courseRepository.save(Course.builder()
                .title("UI/UX Basics")
                .description("User interface and experience design")
                .teacher(smith)
                .category(designCat)
                .price(99.99)
                .durationWeeks(6)
                .students(Set.of(marina))
                .build());

            courseRepository.save(Course.builder()
                .title("Docker & K8s")
                .description("Cloud-native infrastructure guide")
                .teacher(peters)
                .category(devOpsCat)
                .price(250.0)
                .durationWeeks(10)
                .build());

            courseRepository.save(Course.builder()
                .title("React.js Modern")
                .description("Hooks, Context API and Redux")
                .teacher(smith)
                .category(devCat)
                .price(120.0)
                .durationWeeks(8)
                .build());

            Course java = courseRepository.save(Course.builder()
                .title("Java Masterclass")
                .description("Complete Java roadmap for enterprise")
                .teacher(peters)
                .category(devCat)
                .price(199.99)
                .durationWeeks(12)
                .students(Set.of(vlat, marina))
                .build());

            java.setLessons(List.of(
                Lesson.builder().title("Introduction").content("Intro to JVM")
                    .durationMinutes(45).videoUrl("http://vid.us/1")
                    .course(java).build(),
                Lesson.builder().title("Classes").content("OOP Principles")
                    .durationMinutes(60).videoUrl("http://vid.us/2")
                    .course(java).build(),
                Lesson.builder().title("Collections").content("Lists and Maps")
                    .durationMinutes(50).videoUrl("http://vid.us/3")
                    .course(java).build()
            ));

            courseRepository.save(java);

            log.info("Finished: Full data seed with Courses, Teachers, Categories, Students"
                 + " and Lessons.");
        }
    }
}
