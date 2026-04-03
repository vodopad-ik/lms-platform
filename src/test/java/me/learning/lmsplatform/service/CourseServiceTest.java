package me.learning.lmsplatform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.cache.CourseQueryCache;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.CategoryShortDto;
import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.dto.CoursePatchDto;
import me.learning.lmsplatform.dto.LessonCreateDto;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.dto.TeacherShortDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.mapper.CourseMapper;
import me.learning.lmsplatform.mapper.LessonMapper;
import me.learning.lmsplatform.model.Category;
import me.learning.lmsplatform.model.Course;
import me.learning.lmsplatform.model.Lesson;
import me.learning.lmsplatform.model.Student;
import me.learning.lmsplatform.model.Teacher;
import me.learning.lmsplatform.repository.CategoryRepository;
import me.learning.lmsplatform.repository.CourseProjection;
import me.learning.lmsplatform.repository.CourseRepository;
import me.learning.lmsplatform.repository.LessonRepository;
import me.learning.lmsplatform.repository.StudentRepository;
import me.learning.lmsplatform.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

  @Mock
  private CourseRepository courseRepository;

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private LessonRepository lessonRepository;

  @Mock
  private TeacherRepository teacherRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CourseMapper courseMapper;

  @Mock
  private LessonMapper lessonMapper;

  @Mock
  private CourseQueryCache courseQueryCache;

  @Mock
  private CacheInvalidationService cacheInvalidationService;

  @InjectMocks
  private CourseService courseService;

  @Test
  void getAllCourses_shouldReturnAllCourses() {
    Course course1 = new Course();
    course1.setId(1L);
    course1.setTitle("Course 1");

    Course course2 = new Course();
    course2.setId(2L);
    course2.setTitle("Course 2");

    when(courseRepository.findAll()).thenReturn(List.of(course1, course2));
    when(courseMapper.mapToDto(course1)).thenReturn(CourseDto.builder().id(1L).title("Course 1").build());
    when(courseMapper.mapToDto(course2)).thenReturn(CourseDto.builder().id(2L).title("Course 2").build());

    List<CourseDto> result = courseService.getAllCourses();

    assertEquals(2, result.size());
  }

  @Test
  void getCourseById_whenExists_shouldReturnCourse() {
    Course course = new Course();
    course.setId(1L);
    course.setTitle("Test Course");

    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
    when(courseMapper.mapToDto(course)).thenReturn(CourseDto.builder().id(1L).title("Test Course").build());

    CourseDto result = courseService.getCourseById(1L);

    assertNotNull(result);
    assertEquals("Test Course", result.getTitle());
  }

  @Test
  void getCourseById_whenNotExists_shouldThrowException() {
    when(courseRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(999L));
  }

  @Test
  void getCourseByTitle_whenExists_shouldReturnCourse() {
    Course course = new Course();
    course.setId(1L);
    course.setTitle("Java Basics");

    when(courseRepository.findByTitle("Java Basics")).thenReturn(Optional.of(course));
    when(courseMapper.mapToDto(course)).thenReturn(CourseDto.builder().id(1L).title("Java Basics").build());

    CourseDto result = courseService.getCourseByTitle("Java Basics");

    assertNotNull(result);
    assertEquals("Java Basics", result.getTitle());
  }

  @Test
  void getCourseByTitle_whenNotExists_shouldThrowException() {
    when(courseRepository.findByTitle("Unknown")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseByTitle("Unknown"));
  }

  @Test
  void createCourse_withTeacherAndCategory_shouldSaveAndReturnCourse() {
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    Category category = new Category();
    category.setId(1L);

    CourseDto dto = CourseDto.builder()
        .title("New Course")
        .teacher(TeacherShortDto.builder().id(1L).build())
        .category(CategoryShortDto.builder().id(1L).build())
        .build();
    Course entity = new Course();
    entity.setTitle("New Course");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("New Course");
    saved.setTeacher(teacher);
    saved.setCategory(category);

    when(courseMapper.mapToEntity(dto)).thenReturn(entity);
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(courseRepository.save(entity)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("New Course").build());

    CourseDto result = courseService.createCourse(dto);

    assertNotNull(result.getId());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void createCourse_withoutTeacherAndCategory_shouldSaveWithoutAssociations() {
    CourseDto dto = CourseDto.builder().title("New Course").build();
    Course entity = new Course();
    entity.setTitle("New Course");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("New Course");

    when(courseMapper.mapToEntity(dto)).thenReturn(entity);
    when(courseRepository.save(entity)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("New Course").build());

    CourseDto result = courseService.createCourse(dto);

    assertNotNull(result.getId());
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, never()).findById(any());
  }

  @Test
  void updateCourse_whenExists_shouldUpdateAndReturnCourse() {
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    Category category = new Category();
    category.setId(1L);

    CourseDto dto = CourseDto.builder()
        .title("Updated")
        .description("New Desc")
        .price(100.0)
        .durationWeeks(4)
        .teacher(TeacherShortDto.builder().id(1L).build())
        .category(CategoryShortDto.builder().id(1L).build())
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Updated");

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("Updated").build());

    CourseDto result = courseService.updateCourse(1L, dto);

    assertEquals("Updated", result.getTitle());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void updateCourse_whenNotExists_shouldThrowException() {
    CourseDto dto = CourseDto.builder().title("Updated").build();
    when(courseRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(999L, dto));
  }

  @Test
  void patchCourse_withPartialData_shouldUpdateOnlyProvidedFields() {
    CoursePatchDto patchDto = CoursePatchDto.builder()
        .title("Patched Title")
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old Title");
    existing.setDescription("Old Desc");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Patched Title");

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("Patched Title").build());

    CourseDto result = courseService.patchCourse(1L, patchDto);

    assertEquals("Patched Title", result.getTitle());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void patchCourse_withTeacherAndCategory_shouldUpdateAssociations() {
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    Category category = new Category();
    category.setId(1L);

    CoursePatchDto patchDto = CoursePatchDto.builder()
        .teacherId(1L)
        .categoryId(1L)
        .build();
    Course existing = new Course();
    existing.setId(1L);
    Course saved = new Course();
    saved.setId(1L);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).build());

    CourseDto result = courseService.patchCourse(1L, patchDto);

    assertNotNull(result);
    verify(teacherRepository, times(1)).findById(1L);
    verify(categoryRepository, times(1)).findById(1L);
  }

  @Test
  void updateCourse_withoutTeacherAndCategory_shouldSaveWithoutAssociations() {
    CourseDto dto = CourseDto.builder()
        .title("Updated")
        .description("New Desc")
        .price(100.0)
        .durationWeeks(4)
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Updated");

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("Updated").build());

    CourseDto result = courseService.updateCourse(1L, dto);

    assertEquals("Updated", result.getTitle());
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void patchCourse_withNullTeacherAndCategory_shouldNotCallApplyTeacherAndCategory() {
    CoursePatchDto patchDto = CoursePatchDto.builder()
        .title("Patched Title")
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old Title");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Patched Title");

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("Patched Title").build());

    CourseDto result = courseService.patchCourse(1L, patchDto);

    assertEquals("Patched Title", result.getTitle());
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void deleteCourse_shouldDeleteAndInvalidateCache() {
    courseService.deleteCourse(1L);

    verify(courseRepository, times(1)).deleteById(1L);
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void addStudentToCourse_shouldAddStudentAndReturnCourse() {
    Course course = spy(Course.class);
    course.setId(1L);
    Student student = new Student();
    student.setId(1L);

    doReturn(new java.util.HashSet<Student>()).when(course).getStudents();

    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(courseRepository.save(course)).thenReturn(course);
    when(courseMapper.mapToDto(course)).thenReturn(CourseDto.builder().id(1L).build());

    CourseDto result = courseService.addStudentToCourse(1L, 1L);

    assertNotNull(result);
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void addStudentToCourse_whenCourseNotFound_shouldThrowException() {
    when(courseRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.addStudentToCourse(999L, 1L));
  }

  @Test
  void addStudentToCourse_whenStudentNotFound_shouldThrowException() {
    Course course = new Course();
    course.setId(1L);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
    when(studentRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.addStudentToCourse(1L, 999L));
  }

  @Test
  void addLessonToCourse_shouldAddLessonAndReturnLesson() {
    Course course = new Course();
    course.setId(1L);
    LessonCreateDto lessonDto = LessonCreateDto.builder().title("New Lesson").build();
    Lesson lesson = new Lesson();
    lesson.setTitle("New Lesson");
    Lesson saved = new Lesson();
    saved.setId(1L);
    saved.setTitle("New Lesson");
    saved.setCourse(course);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
    when(lessonMapper.mapCreateToEntity(lessonDto)).thenReturn(lesson);
    when(lessonRepository.save(lesson)).thenReturn(saved);
    when(lessonMapper.mapToDto(saved)).thenReturn(LessonDto.builder().id(1L).title("New Lesson").build());

    LessonDto result = courseService.addLessonToCourse(1L, lessonDto);

    assertNotNull(result.getId());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void getLessonsByCourseId_whenCourseExists_shouldReturnLessons() {
    Lesson lesson = new Lesson();
    lesson.setId(1L);
    lesson.setTitle("Lesson");

    when(courseRepository.existsById(1L)).thenReturn(true);
    when(lessonRepository.findByCourseId(1L)).thenReturn(List.of(lesson));
    when(lessonMapper.mapToDto(lesson)).thenReturn(LessonDto.builder().id(1L).title("Lesson").build());

    List<LessonDto> result = courseService.getLessonsByCourseId(1L);

    assertEquals(1, result.size());
  }

  @Test
  void getLessonsByCourseId_whenCourseNotExists_shouldThrowException() {
    when(courseRepository.existsById(999L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> courseService.getLessonsByCourseId(999L));
  }

  @Test
  void searchCourses_withJpqlMode_shouldReturnResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Course course = new Course();
    course.setId(1L);
    course.setTitle("Test");
    Page<Course> page = new PageImpl<>(List.of(course), pageable, 1);

    when(courseQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(courseRepository.findWithFilters("dept", "cat", 10.0, 100.0, pageable)).thenReturn(page);
    when(courseMapper.mapToDto(course)).thenReturn(CourseDto.builder().id(1L).title("Test").build());

    Page<CourseDto> result = courseService.searchCourses("dept", "cat", 10.0, 100.0, pageable, QueryMode.JPQL);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void searchCourses_withNativeMode_shouldReturnResults() {
    Pageable pageable = PageRequest.of(0, 10);
    CourseProjection projection = new CourseProjection() {
      @Override
      public Long getId() { return 1L; }
      @Override
      public String getTitle() { return "Test"; }
      @Override
      public String getDescription() { return "Desc"; }
      @Override
      public Double getPrice() { return 100.0; }
      @Override
      public Integer getDurationWeeks() { return 8; }
      @Override
      public Long getTeacherId() { return 1L; }
      @Override
      public String getTeacherName() { return "Teacher"; }
      @Override
      public Long getCategoryId() { return 1L; }
      @Override
      public String getCategoryName() { return "Category"; }
    };
    Page<CourseProjection> page = new PageImpl<>(List.of(projection), pageable, 1);

    when(courseQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(courseRepository.findWithFiltersNative("dept", "cat", 10.0, 100.0, pageable)).thenReturn(page);
    when(courseMapper.mapToDto(any(CourseProjection.class))).thenReturn(CourseDto.builder().id(1L).title("Test").build());

    Page<CourseDto> result = courseService.searchCourses("dept", "cat", 10.0, 100.0, pageable, QueryMode.NATIVE);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void createCourse_whenTeacherNotFound_shouldThrowException() {
    CourseDto dto = CourseDto.builder()
        .title("New Course")
        .teacher(TeacherShortDto.builder().id(999L).build())
        .build();
    Course entity = new Course();
    entity.setTitle("New Course");

    when(courseMapper.mapToEntity(dto)).thenReturn(entity);
    when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.createCourse(dto));
  }

  @Test
  void createCourse_whenCategoryNotFound_shouldThrowException() {
    CourseDto dto = CourseDto.builder()
        .title("New Course")
        .category(CategoryShortDto.builder().id(999L).build())
        .build();
    Course entity = new Course();
    entity.setTitle("New Course");

    when(courseMapper.mapToEntity(dto)).thenReturn(entity);
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.createCourse(dto));
  }

  @Test
  void updateCourse_whenTeacherNotFound_shouldThrowException() {
    CourseDto dto = CourseDto.builder()
        .title("Updated")
        .teacher(TeacherShortDto.builder().id(999L).build())
        .build();
    Course existing = new Course();
    existing.setId(1L);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(1L, dto));
  }

  @Test
  void updateCourse_whenCategoryNotFound_shouldThrowException() {
    CourseDto dto = CourseDto.builder()
        .title("Updated")
        .category(CategoryShortDto.builder().id(999L).build())
        .build();
    Course existing = new Course();
    existing.setId(1L);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(1L, dto));
  }

  @Test
  void patchCourse_whenTeacherNotFound_shouldThrowException() {
    CoursePatchDto patchDto = CoursePatchDto.builder()
        .teacherId(999L)
        .build();
    Course existing = new Course();
    existing.setId(1L);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.patchCourse(1L, patchDto));
  }

  @Test
  void patchCourse_whenCategoryNotFound_shouldThrowException() {
    CoursePatchDto patchDto = CoursePatchDto.builder()
        .categoryId(999L)
        .build();
    Course existing = new Course();
    existing.setId(1L);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> courseService.patchCourse(1L, patchDto));
  }

  @Test
  void patchCourse_withOnlyNullIds_shouldNotApplyTeacherOrCategory() {
    CoursePatchDto patchDto = CoursePatchDto.builder()
        .title("Patched Title")
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old Title");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Patched Title");

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("Patched Title").build());

    CourseDto result = courseService.patchCourse(1L, patchDto);

    assertEquals("Patched Title", result.getTitle());
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void createCourse_withOnlyTeacherNull_shouldSaveWithCategory() {
    Category category = new Category();
    category.setId(1L);

    CourseDto dto = CourseDto.builder()
        .title("New Course")
        .category(CategoryShortDto.builder().id(1L).build())
        .build();
    Course entity = new Course();
    entity.setTitle("New Course");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("New Course");
    saved.setCategory(category);

    when(courseMapper.mapToEntity(dto)).thenReturn(entity);
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(courseRepository.save(entity)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("New Course").build());

    CourseDto result = courseService.createCourse(dto);

    assertNotNull(result.getId());
    verify(teacherRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void updateCourse_withOnlyTeacher_shouldSaveWithTeacher() {
    Teacher teacher = new Teacher();
    teacher.setId(1L);

    CourseDto dto = CourseDto.builder()
        .title("Updated")
        .description("New Desc")
        .price(100.0)
        .durationWeeks(4)
        .teacher(TeacherShortDto.builder().id(1L).build())
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Updated");

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("Updated").build());

    CourseDto result = courseService.updateCourse(1L, dto);

    assertEquals("Updated", result.getTitle());
    verify(teacherRepository, times(1)).findById(1L);
    verify(categoryRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void updateCourse_withOnlyCategory_shouldSaveWithCategory() {
    Category category = new Category();
    category.setId(1L);

    CourseDto dto = CourseDto.builder()
        .title("Updated")
        .description("New Desc")
        .price(100.0)
        .durationWeeks(4)
        .category(CategoryShortDto.builder().id(1L).build())
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Updated");

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).title("Updated").build());

    CourseDto result = courseService.updateCourse(1L, dto);

    assertEquals("Updated", result.getTitle());
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, times(1)).findById(1L);
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void patchCourse_withDescriptionPriceDurationWeeks_shouldUpdateAllFields() {
    CoursePatchDto patchDto = CoursePatchDto.builder()
        .description("New Desc")
        .price(199.99)
        .durationWeeks(12)
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old Title");
    existing.setDescription("Old Desc");
    existing.setPrice(100.0);
    existing.setDurationWeeks(8);
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Old Title");
    saved.setDescription("New Desc");
    saved.setPrice(199.99);
    saved.setDurationWeeks(12);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).description("New Desc").price(199.99).durationWeeks(12).build());

    CourseDto result = courseService.patchCourse(1L, patchDto);

    assertEquals("New Desc", result.getDescription());
    assertEquals(199.99, result.getPrice());
    assertEquals(12, result.getDurationWeeks());
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void patchCourse_withOnlyDescription_shouldUpdateDescription() {
    CoursePatchDto patchDto = CoursePatchDto.builder()
        .description("New Description")
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old Title");
    existing.setDescription("Old Desc");
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Old Title");
    saved.setDescription("New Description");

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).description("New Description").build());

    CourseDto result = courseService.patchCourse(1L, patchDto);

    assertEquals("New Description", result.getDescription());
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void patchCourse_withOnlyPrice_shouldUpdatePrice() {
    CoursePatchDto patchDto = CoursePatchDto.builder()
        .price(299.99)
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old Title");
    existing.setPrice(100.0);
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Old Title");
    saved.setPrice(299.99);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).price(299.99).build());

    CourseDto result = courseService.patchCourse(1L, patchDto);

    assertEquals(299.99, result.getPrice());
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void patchCourse_withOnlyDurationWeeks_shouldUpdateDurationWeeks() {
    CoursePatchDto patchDto = CoursePatchDto.builder()
        .durationWeeks(16)
        .build();
    Course existing = new Course();
    existing.setId(1L);
    existing.setTitle("Old Title");
    existing.setDurationWeeks(8);
    Course saved = new Course();
    saved.setId(1L);
    saved.setTitle("Old Title");
    saved.setDurationWeeks(16);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).durationWeeks(16).build());

    CourseDto result = courseService.patchCourse(1L, patchDto);

    assertEquals(16, result.getDurationWeeks());
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, never()).findById(any());
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }

  @Test
  void patchCourse_withOnlyCategory_shouldUpdateCategory() {
    Category category = new Category();
    category.setId(1L);

    CoursePatchDto patchDto = CoursePatchDto.builder()
        .categoryId(1L)
        .build();
    Course existing = new Course();
    existing.setId(1L);
    Course saved = new Course();
    saved.setId(1L);

    when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(courseRepository.save(existing)).thenReturn(saved);
    when(courseMapper.mapToDto(saved)).thenReturn(CourseDto.builder().id(1L).build());

    CourseDto result = courseService.patchCourse(1L, patchDto);

    assertNotNull(result);
    verify(teacherRepository, never()).findById(any());
    verify(categoryRepository, times(1)).findById(1L);
    verify(cacheInvalidationService, times(1)).onCourseChanged();
  }
}
