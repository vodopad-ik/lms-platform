package me.learning.lmsplatform.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.dto.CoursePatchDto;
import me.learning.lmsplatform.dto.LessonCreateDto;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.mapper.CourseMapper;
import me.learning.lmsplatform.mapper.LessonMapper;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

    private static final String NOT_FOUND_MSG = "Course not found with id: ";
    private static final String STUDENT_NOT_FOUND_MSG = "Student not found with id: ";
    private static final String TEACHER_NOT_FOUND_MSG = "Teacher not found with id: ";
    private static final String CATEGORY_NOT_FOUND_MSG = "Category not found with id: ";

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final TeacherRepository teacherRepository;
    private final CategoryRepository categoryRepository;
    private final CourseMapper courseMapper;
    private final LessonMapper lessonMapper;

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::mapToDto)
                .toList();
    }

    public CourseDto getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(courseMapper::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
    }

    public CourseDto getCourseByTitle(String title) {
        return courseRepository.findByTitle(title)
                .map(courseMapper::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with title: " + title));
    }

    public CourseDto createCourse(CourseDto courseDto) {
        Course course = courseMapper.mapToEntity(courseDto);
        applyTeacherAndCategory(course, courseDto.getTeacherId(), courseDto.getCategoryId());
        return courseMapper.mapToDto(courseRepository.save(course));
    }

    public CourseDto updateCourse(Long id, CourseDto courseDto) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
        existing.setTitle(courseDto.getTitle());
        existing.setDescription(courseDto.getDescription());
        existing.setPrice(courseDto.getPrice());
        existing.setDurationWeeks(courseDto.getDurationWeeks());
        applyTeacherAndCategory(existing, courseDto.getTeacherId(), courseDto.getCategoryId());
        return courseMapper.mapToDto(courseRepository.save(existing));
    }

    public CourseDto patchCourse(Long id, CoursePatchDto patchDto) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
        if (patchDto.getTitle() != null) {
            existing.setTitle(patchDto.getTitle());
        }
        if (patchDto.getDescription() != null) {
            existing.setDescription(patchDto.getDescription());
        }
        if (patchDto.getPrice() != null) {
            existing.setPrice(patchDto.getPrice());
        }
        if (patchDto.getDurationWeeks() != null) {
            existing.setDurationWeeks(patchDto.getDurationWeeks());
        }
        if (patchDto.getTeacherId() != null || patchDto.getCategoryId() != null) {
            applyTeacherAndCategory(existing, patchDto.getTeacherId(), patchDto.getCategoryId());
        }
        return courseMapper.mapToDto(courseRepository.save(existing));
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public CourseDto addStudentToCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + courseId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        STUDENT_NOT_FOUND_MSG + studentId));
        course.getStudents().add(student);
        return courseMapper.mapToDto(courseRepository.save(course));
    }

    public LessonDto addLessonToCourse(Long courseId, LessonCreateDto lessonDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + courseId));
        Lesson lesson = lessonMapper.mapCreateToEntity(lessonDto);
        lesson.setCourse(course);
        return lessonMapper.mapToDto(lessonRepository.save(lesson));
    }

    public List<LessonDto> getLessonsByCourseId(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException(NOT_FOUND_MSG + courseId);
        }
        return lessonRepository.findByCourseId(courseId).stream()
                .map(lessonMapper::mapToDto)
                .toList();
    }

    private void applyTeacherAndCategory(Course course, Long teacherId, Long categoryId) {
        if (teacherId != null) {
            Teacher teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            TEACHER_NOT_FOUND_MSG + teacherId));
            course.setTeacher(teacher);
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            CATEGORY_NOT_FOUND_MSG + categoryId));
            course.setCategory(category);
        }
    }
}
