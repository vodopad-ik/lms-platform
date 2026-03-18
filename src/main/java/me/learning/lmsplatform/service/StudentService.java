package me.learning.lmsplatform.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.cache.StudentQueryCache;
import me.learning.lmsplatform.cache.StudentQueryCacheKey;
import me.learning.lmsplatform.dto.StudentDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.mapper.StudentMapper;
import me.learning.lmsplatform.model.Student;
import me.learning.lmsplatform.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

  private static final String NOT_FOUND_MSG = "Student not found with id: ";

  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;
  private final StudentQueryCache studentQueryCache;
  private final CacheInvalidationService cacheInvalidationService;

  public List<StudentDto> getAllStudents() {
    return studentRepository.findAll().stream()
        .map(studentMapper::mapToDto)
        .toList();
  }

  public StudentDto getStudentById(Long id) {
    return studentRepository.findById(id)
        .map(studentMapper::mapToDto)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
  }

  public StudentDto createStudent(StudentDto studentDto) {
    Student student = studentMapper.mapToEntity(studentDto);
    Student saved = studentRepository.save(student);
    cacheInvalidationService.onStudentChanged();
    return studentMapper.mapToDto(saved);
  }

  public StudentDto updateStudent(Long id, StudentDto studentDto) {
    Student existing = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
    existing.setName(studentDto.getName());
    existing.setEmail(studentDto.getEmail());
    existing.setEnrollmentDate(studentDto.getEnrollmentDate());
    Student saved = studentRepository.save(existing);
    cacheInvalidationService.onStudentChanged();
    return studentMapper.mapToDto(saved);
  }

  public void deleteStudent(Long id) {
    studentRepository.deleteById(id);
    cacheInvalidationService.onStudentChanged();
  }

  public Page<StudentDto> searchStudents(
      String nameFilter,
      String emailFilter,
      String courseTitle,
      Pageable pageable,
      QueryMode queryMode) {
    String normalizedNameFilter = normalizeLike(nameFilter);
    String normalizedEmailFilter = normalizeLike(emailFilter);
    String normalizedCourseTitle = normalizeLike(courseTitle);
    StudentQueryCacheKey key = StudentQueryCacheKey.from(
        queryMode, normalizedNameFilter, normalizedEmailFilter, normalizedCourseTitle, pageable);
    return studentQueryCache.getOrLoad(
        key,
        () -> fetchStudentsWithFilters(
            normalizedNameFilter,
            normalizedEmailFilter,
            normalizedCourseTitle,
            pageable,
            queryMode));
  }

  private Page<StudentDto> fetchStudentsWithFilters(
      String nameFilter,
      String emailFilter,
      String courseTitle,
      Pageable pageable,
      QueryMode queryMode) {
    if (queryMode == QueryMode.NATIVE) {
      Page<Student> nativePage = studentRepository.findWithFiltersNative(
          nameFilter, emailFilter, courseTitle, pageable);
      return mapStudentPage(nativePage, pageable);
    }
    return studentRepository.findWithFilters(nameFilter, emailFilter, courseTitle, pageable)
        .map(studentMapper::mapToDto);
  }

  private Page<StudentDto> mapStudentPage(Page<Student> studentPage, Pageable pageable) {
    return new PageImpl<>(
        studentPage.stream()
            .map(studentMapper::mapToDto)
            .toList(),
        pageable,
        studentPage.getTotalElements());
  }

  // cache invalidation handled by CacheInvalidationService

  private static String normalizeLike(String value) {
    return value == null ? "" : value;
  }
}
