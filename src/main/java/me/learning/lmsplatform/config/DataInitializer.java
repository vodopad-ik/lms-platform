package me.learning.lmsplatform.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import me.learning.lmsplatform.repository.LessonRepository;
import me.learning.lmsplatform.repository.StudentRepository;
import me.learning.lmsplatform.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private static final String CATEGORY_DEVELOPMENT = "Development";
    private static final String CATEGORY_DATA_SCIENCE = "Data Science";
    private static final String CATEGORY_DESIGN = "Design";
    private static final String CATEGORY_MARKETING = "Marketing";
    private static final String CATEGORY_BUSINESS = "Business";
    private static final String CATEGORY_DEVOPS = "DevOps";

    private record TeacherSeed(String name, String email, String department,
                               int experienceYears) {
    }

    private record StudentSeed(String name, String email, LocalDate enrollmentDate,
                               String primaryCategory, String secondaryCategory) {
    }

    private record CourseTrack(String title, String summary, String categoryName,
                               String teacherEmail, double basePrice,
                               int baseDurationWeeks) {
    }

    private record SeededStudent(Student entity, String primaryCategory,
                                 String secondaryCategory) {
    }

    private static final List<String> CATEGORY_NAMES = List.of(
        CATEGORY_DEVELOPMENT,
        CATEGORY_DATA_SCIENCE,
        CATEGORY_DESIGN,
        CATEGORY_MARKETING,
        CATEGORY_BUSINESS,
        CATEGORY_DEVOPS
    );

    private static final List<String> COURSE_LEVELS = List.of(
        "Foundations",
        "Practitioner",
        "Advanced",
        "Workshop",
        "Capstone"
    );

    private static final List<String> LESSON_NAMES = List.of(
        "Kickoff",
        "Guided Practice",
        "Project Review"
    );

    private static final List<TeacherSeed> TEACHER_SEEDS = List.of(
        new TeacherSeed("Dr. Amelia Peters", "amelia.peters@lms.demo",
            "Computer Science", 15),
        new TeacherSeed("Alice Smith", "alice.smith@lms.demo",
            "Software Engineering", 9),
        new TeacherSeed("Omar Hassan", "omar.hassan@lms.demo",
            "Cloud Infrastructure", 11),
        new TeacherSeed("Lena Morozova", "lena.morozova@lms.demo",
            "Data Analytics", 8),
        new TeacherSeed("Victor Chen", "victor.chen@lms.demo",
            "Machine Learning", 10),
        new TeacherSeed("Diana Park", "diana.park@lms.demo",
            "Product Design", 7),
        new TeacherSeed("Marta Alvarez", "marta.alvarez@lms.demo",
            "Digital Marketing", 9),
        new TeacherSeed("Sofia Bennett", "sofia.bennett@lms.demo",
            "Business Strategy", 12),
        new TeacherSeed("Elena Volkova", "elena.volkova@lms.demo",
            "Frontend Engineering", 6),
        new TeacherSeed("Igor Sokolov", "igor.sokolov@lms.demo", "Product Operations", 13)
    );

    private static final List<StudentSeed> STUDENT_SEEDS = List.of(
        new StudentSeed("Anna Petrova", "anna.petrova@students.demo",
            LocalDate.now().minusDays(90), CATEGORY_DEVELOPMENT, CATEGORY_DESIGN),
        new StudentSeed("Ivan Kozlov", "ivan.kozlov@students.demo",
            LocalDate.now().minusDays(86), CATEGORY_DEVELOPMENT, CATEGORY_DEVOPS),
        new StudentSeed("Maria Sidorova", "maria.sidorova@students.demo",
            LocalDate.now().minusDays(82), CATEGORY_DATA_SCIENCE, CATEGORY_BUSINESS),
        new StudentSeed("Pavel Smirnov", "pavel.smirnov@students.demo",
            LocalDate.now().minusDays(78), CATEGORY_MARKETING, CATEGORY_BUSINESS),
        new StudentSeed("Olga Romanova", "olga.romanova@students.demo",
            LocalDate.now().minusDays(74), CATEGORY_DESIGN, CATEGORY_MARKETING),
        new StudentSeed("Nikita Fedorov", "nikita.fedorov@students.demo",
            LocalDate.now().minusDays(70), CATEGORY_DEVOPS, CATEGORY_DEVELOPMENT),
        new StudentSeed("Elena Volk", "elena.volk@students.demo",
            LocalDate.now().minusDays(66), CATEGORY_DATA_SCIENCE, CATEGORY_DEVELOPMENT),
        new StudentSeed("Denis Moroz", "denis.moroz@students.demo",
            LocalDate.now().minusDays(62), CATEGORY_BUSINESS, CATEGORY_MARKETING),
        new StudentSeed("Svetlana Orlova", "svetlana.orlova@students.demo",
            LocalDate.now().minusDays(58), CATEGORY_DESIGN, CATEGORY_BUSINESS),
        new StudentSeed("Kirill Antonov", "kirill.antonov@students.demo",
            LocalDate.now().minusDays(54), CATEGORY_DEVELOPMENT, CATEGORY_DATA_SCIENCE),
        new StudentSeed("Alina Belova", "alina.belova@students.demo",
            LocalDate.now().minusDays(50), CATEGORY_MARKETING, CATEGORY_DESIGN),
        new StudentSeed("Roman Egorov", "roman.egorov@students.demo",
            LocalDate.now().minusDays(46), CATEGORY_DEVOPS, CATEGORY_DATA_SCIENCE),
        new StudentSeed("Yulia Makarova", "yulia.makarova@students.demo",
            LocalDate.now().minusDays(42), CATEGORY_DEVELOPMENT, CATEGORY_BUSINESS),
        new StudentSeed("Artem Pavlov", "artem.pavlov@students.demo",
            LocalDate.now().minusDays(38), CATEGORY_DATA_SCIENCE, CATEGORY_DEVOPS),
        new StudentSeed("Polina Zaitseva", "polina.zaitseva@students.demo",
            LocalDate.now().minusDays(34), CATEGORY_DESIGN, CATEGORY_MARKETING),
        new StudentSeed("Maksim Lebedev", "maksim.lebedev@students.demo",
            LocalDate.now().minusDays(30), CATEGORY_BUSINESS, CATEGORY_DEVELOPMENT),
        new StudentSeed("Veronika Klimova", "veronika.klimova@students.demo",
            LocalDate.now().minusDays(26), CATEGORY_MARKETING, CATEGORY_BUSINESS),
        new StudentSeed("Timur Gromov", "timur.gromov@students.demo",
            LocalDate.now().minusDays(22), CATEGORY_DEVOPS, CATEGORY_DEVELOPMENT),
        new StudentSeed("Ekaterina Sorokina", "ekaterina.sorokina@students.demo",
            LocalDate.now().minusDays(18), CATEGORY_DATA_SCIENCE, CATEGORY_DESIGN),
        new StudentSeed("Ilya Voronov", "ilya.voronov@students.demo",
            LocalDate.now().minusDays(14), CATEGORY_DEVELOPMENT, CATEGORY_DEVOPS)
    );

    private static final List<CourseTrack> COURSE_TRACKS = List.of(
        new CourseTrack("Java Backend", "Build enterprise-grade backend services",
            CATEGORY_DEVELOPMENT, "amelia.peters@lms.demo", 129.99, 6),
        new CourseTrack("Spring Boot APIs",
            "Design clean REST APIs and business workflows",
            CATEGORY_DEVELOPMENT, "alice.smith@lms.demo", 149.99, 7),
        new CourseTrack("React Frontend",
            "Deliver modern, maintainable client interfaces",
            CATEGORY_DEVELOPMENT, "elena.volkova@lms.demo", 139.99, 6),
        new CourseTrack("Python Analytics",
            "Analyze real datasets and automate reporting",
            CATEGORY_DATA_SCIENCE, "lena.morozova@lms.demo", 159.99, 7),
        new CourseTrack("Machine Learning",
            "Train and evaluate practical ML pipelines",
            CATEGORY_DATA_SCIENCE, "victor.chen@lms.demo", 189.99, 8),
        new CourseTrack("Product Design",
            "Create user-centered product experiences",
            CATEGORY_DESIGN, "diana.park@lms.demo", 119.99, 6),
        new CourseTrack("Digital Marketing",
            "Plan channels, funnels, and campaign execution",
            CATEGORY_MARKETING, "marta.alvarez@lms.demo", 109.99, 5),
        new CourseTrack("Business Operations",
            "Structure repeatable processes for growing teams",
            CATEGORY_BUSINESS, "sofia.bennett@lms.demo", 129.99, 6),
        new CourseTrack("Product Management",
            "Turn product strategy into execution plans",
            CATEGORY_BUSINESS, "igor.sokolov@lms.demo", 149.99, 6),
        new CourseTrack("Cloud & DevOps",
            "Operate delivery pipelines and cloud platforms",
            CATEGORY_DEVOPS, "omar.hassan@lms.demo", 169.99, 7)
    );

    private static final String LEGACY_VIDEO_URL_PREFIX = "https://example.com/";

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final CategoryRepository categoryRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;

    @Override
    public void run(String... args) {
        migrateLegacyVideoUrls();
        cleanupLoadTestData();

        if (courseRepository.count() > 0) {
            log.info("Skipping local seed because courses already exist.");
            return;
        }

        Map<String, Category> categories = seedCategories();
        Map<String, Teacher> teachers = seedTeachers();
        List<SeededStudent> students = seedStudents();
        int courseCount = seedCourses(categories, teachers, students);

        log.info(
            "Finished curated local seed: {} categories, {} teachers, {} students, "
                + "{} courses, {} lessons.",
            categories.size(), teachers.size(), students.size(), courseCount,
            courseCount * LESSON_NAMES.size());
    }

    private Map<String, Category> seedCategories() {
        Map<String, Category> categories = new LinkedHashMap<>();
        for (String categoryName : CATEGORY_NAMES) {
            Category category = categoryRepository.save(Category.builder()
                .name(categoryName)
                .build());
            categories.put(categoryName, category);
        }
        return categories;
    }

    private Map<String, Teacher> seedTeachers() {
        Map<String, Teacher> teachers = new LinkedHashMap<>();
        for (TeacherSeed seed : TEACHER_SEEDS) {
            Teacher teacher = teacherRepository.save(Teacher.builder()
                .name(seed.name())
                .email(seed.email())
                .department(seed.department())
                .experienceYears(seed.experienceYears())
                .build());
            teachers.put(seed.email(), teacher);
        }
        return teachers;
    }

    private List<SeededStudent> seedStudents() {
        List<SeededStudent> students = new ArrayList<>();
        for (StudentSeed seed : STUDENT_SEEDS) {
            Student student = studentRepository.save(Student.builder()
                .name(seed.name())
                .email(seed.email())
                .enrollmentDate(seed.enrollmentDate())
                .build());
            students.add(
                new SeededStudent(student, seed.primaryCategory(), seed.secondaryCategory()));
        }
        return students;
    }

    private int seedCourses(Map<String, Category> categories,
                            Map<String, Teacher> teachers,
                            List<SeededStudent> students) {
        int courseCount = 0;

        for (CourseTrack track : COURSE_TRACKS) {
            Category category = categories.get(track.categoryName());
            Teacher teacher = teachers.get(track.teacherEmail());
            List<SeededStudent> relevantStudents = students.stream()
                .filter(student -> track.categoryName().equals(student.primaryCategory())
                    || track.categoryName().equals(student.secondaryCategory()))
                .toList();

            for (int levelIndex = 0; levelIndex < COURSE_LEVELS.size(); levelIndex++) {
                String level = COURSE_LEVELS.get(levelIndex);
                Course course = courseRepository.save(Course.builder()
                    .title(track.title() + " " + level)
                    .description(track.summary()
                        + " This track focuses on "
                        + level.toLowerCase(Locale.ROOT)
                        + " outcomes and realistic practice.")
                    .teacher(teacher)
                    .category(category)
                    .price(track.basePrice() + (levelIndex * 20.0))
                    .durationWeeks(track.baseDurationWeeks() + (levelIndex % 2))
                    .students(
                        selectStudents(relevantStudents, levelIndex, track.title().length()))
                    .build());

                course.setLessons(buildLessons(course, levelIndex));
                courseRepository.save(course);
                courseCount++;
            }
        }

        return courseCount;
    }

    private Set<Student> selectStudents(List<SeededStudent> relevantStudents,
                                        int levelIndex,
                                        int rotationSeed) {
        Set<Student> selectedStudents = new LinkedHashSet<>();
        int targetSize = Math.min(5, relevantStudents.size());
        int startIndex = (rotationSeed + levelIndex) % relevantStudents.size();

        for (int i = 0; i < targetSize; i++) {
            SeededStudent seededStudent = relevantStudents.get(
                (startIndex + i) % relevantStudents.size());
            selectedStudents.add(seededStudent.entity());
        }

        return selectedStudents;
    }

    private List<Lesson> buildLessons(Course course, int levelIndex) {
        List<Lesson> lessons = new ArrayList<>();

        for (int lessonIndex = 0; lessonIndex < LESSON_NAMES.size(); lessonIndex++) {
            String lessonName = LESSON_NAMES.get(lessonIndex);
            lessons.add(Lesson.builder()
                .title(lessonName)
                .content("Hands-on lesson for " + course.getTitle()
                    + " covering a concrete part of the track.")
                .durationMinutes(35 + (lessonIndex * 10) + (levelIndex * 5))
                .videoUrl(buildVideoUrl(course.getTitle(), lessonName))
                .course(course)
                .build());
        }

        return lessons;
    }

    private String buildVideoUrl(String courseTitle, String lessonName) {
        String query = courseTitle + " " + lessonName + " tutorial";
        return "https://www.youtube.com/results?search_query="
            + URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    private void cleanupLoadTestData() {
        List<Course> loadTestCourses = courseRepository.findAll().stream()
            .filter(c -> c.getDescription() != null && c.getDescription().contains("Load test"))
            .toList();
        log.info("Found {} courses for cleanup.", loadTestCourses.size());
        if (loadTestCourses.isEmpty()) {
            return;
        }
        courseRepository.deleteAll(loadTestCourses);
        log.info("Cleaned up {} load test courses from database.", loadTestCourses.size());
    }

    private void migrateLegacyVideoUrls() {
        List<Lesson> legacyLessons = lessonRepository.findAll().stream()
            .filter(lesson -> lesson.getVideoUrl() != null
                && lesson.getVideoUrl().startsWith(LEGACY_VIDEO_URL_PREFIX))
            .toList();
        if (legacyLessons.isEmpty()) {
            return;
        }
        for (Lesson lesson : legacyLessons) {
            String courseTitle = lesson.getCourse() != null
                ? lesson.getCourse().getTitle()
                : "course";
            lesson.setVideoUrl(buildVideoUrl(courseTitle, lesson.getTitle()));
        }
        lessonRepository.saveAll(legacyLessons);
        log.info("Migrated {} legacy lesson video URLs to real links.",
            legacyLessons.size());
    }

}
