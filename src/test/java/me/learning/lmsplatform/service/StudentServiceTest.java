package me.learning.lmsplatform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.cache.StudentQueryCache;
import me.learning.lmsplatform.dto.StudentDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.mapper.StudentMapper;
import me.learning.lmsplatform.model.Student;
import me.learning.lmsplatform.repository.StudentRepository;
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
class StudentServiceTest {

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private StudentMapper studentMapper;

  @Mock
  private StudentQueryCache studentQueryCache;

  @Mock
  private CacheInvalidationService cacheInvalidationService;

  @InjectMocks
  private StudentService studentService;

  @Test
  void getAllStudents_shouldReturnAllStudents() {
    Student student1 = new Student();
    student1.setId(1L);
    student1.setName("Student 1");

    Student student2 = new Student();
    student2.setId(2L);
    student2.setName("Student 2");

    when(studentRepository.findAll()).thenReturn(List.of(student1, student2));
    when(studentMapper.mapToDto(student1)).thenReturn(StudentDto.builder().id(1L).name("Student 1").build());
    when(studentMapper.mapToDto(student2)).thenReturn(StudentDto.builder().id(2L).name("Student 2").build());

    List<StudentDto> result = studentService.getAllStudents();

    assertEquals(2, result.size());
  }

  @Test
  void getStudentById_whenExists_shouldReturnStudent() {
    Student student = new Student();
    student.setId(1L);
    student.setName("Test Student");

    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(studentMapper.mapToDto(student)).thenReturn(StudentDto.builder().id(1L).name("Test Student").build());

    StudentDto result = studentService.getStudentById(1L);

    assertNotNull(result);
    assertEquals("Test Student", result.getName());
  }

  @Test
  void getStudentById_whenNotExists_shouldThrowException() {
    when(studentRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById(999L));
  }

  @Test
  void createStudent_shouldSaveAndReturnStudent() {
    StudentDto dto = StudentDto.builder().name("New Student").email("test@test.com").build();
    Student entity = new Student();
    entity.setName("New Student");
    Student saved = new Student();
    saved.setId(1L);
    saved.setName("New Student");

    when(studentMapper.mapToEntity(dto)).thenReturn(entity);
    when(studentRepository.save(entity)).thenReturn(saved);
    when(studentMapper.mapToDto(saved)).thenReturn(StudentDto.builder().id(1L).name("New Student").build());

    StudentDto result = studentService.createStudent(dto);

    assertNotNull(result.getId());
    verify(cacheInvalidationService, times(1)).onStudentChanged();
  }

  @Test
  void updateStudent_whenExists_shouldUpdateAndReturnStudent() {
    StudentDto dto = StudentDto.builder().name("Updated").email("updated@test.com").build();
    Student existing = new Student();
    existing.setId(1L);
    existing.setName("Old");
    Student saved = new Student();
    saved.setId(1L);
    saved.setName("Updated");

    when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(studentRepository.save(existing)).thenReturn(saved);
    when(studentMapper.mapToDto(saved)).thenReturn(StudentDto.builder().id(1L).name("Updated").build());

    StudentDto result = studentService.updateStudent(1L, dto);

    assertEquals("Updated", result.getName());
    verify(cacheInvalidationService, times(1)).onStudentChanged();
  }

  @Test
  void updateStudent_whenNotExists_shouldThrowException() {
    StudentDto dto = StudentDto.builder().name("Updated").build();
    when(studentRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> studentService.updateStudent(999L, dto));
  }

  @Test
  void deleteStudent_shouldDeleteAndInvalidateCache() {
    studentService.deleteStudent(1L);

    verify(studentRepository, times(1)).deleteById(1L);
    verify(cacheInvalidationService, times(1)).onStudentChanged();
  }

  @Test
  void searchStudents_withJpqlMode_shouldReturnResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Student student = new Student();
    student.setId(1L);
    student.setName("Test");
    Page<Student> page = new PageImpl<>(List.of(student), pageable, 1);

    when(studentQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(studentRepository.findWithFilters("test", "email", "course", pageable)).thenReturn(page);
    when(studentMapper.mapToDto(student)).thenReturn(StudentDto.builder().id(1L).name("Test").build());

    Page<StudentDto> result = studentService.searchStudents("test", "email", "course", pageable, QueryMode.JPQL);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void searchStudents_withNativeMode_shouldReturnResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Student student = new Student();
    student.setId(1L);
    student.setName("Test");
    Page<Student> page = new PageImpl<>(List.of(student), pageable, 1);

    when(studentQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(studentRepository.findWithFiltersNative("test", "email", "course", pageable)).thenReturn(page);
    when(studentMapper.mapToDto(student)).thenReturn(StudentDto.builder().id(1L).name("Test").build());

    Page<StudentDto> result = studentService.searchStudents("test", "email", "course", pageable, QueryMode.NATIVE);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void searchStudents_withNullFilters_shouldNormalizeToEmptyString() {
    Pageable pageable = PageRequest.of(0, 10);
    Student student = new Student();
    student.setId(1L);
    student.setName("Test");
    Page<Student> page = new PageImpl<>(List.of(student), pageable, 1);

    when(studentQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(studentRepository.findWithFilters("", "", "", pageable)).thenReturn(page);
    when(studentMapper.mapToDto(student)).thenReturn(StudentDto.builder().id(1L).name("Test").build());

    Page<StudentDto> result = studentService.searchStudents(null, null, null, pageable, QueryMode.JPQL);

    assertEquals(1, result.getTotalElements());
  }
}
