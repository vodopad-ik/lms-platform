package me.learning.lmsplatform.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.cache.TeacherQueryCache;
import me.learning.lmsplatform.cache.TeacherQueryCacheKey;
import me.learning.lmsplatform.dto.TeacherDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.mapper.TeacherMapper;
import me.learning.lmsplatform.model.Teacher;
import me.learning.lmsplatform.repository.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

  private static final String NOT_FOUND_MSG = "Teacher not found with id: ";

  private final TeacherRepository teacherRepository;
  private final TeacherMapper teacherMapper;
  private final TeacherQueryCache teacherQueryCache;
  private final CacheInvalidationService cacheInvalidationService;

  public List<TeacherDto> getAllTeachers() {
    return teacherRepository.findAll().stream()
        .map(teacherMapper::mapToDto)
        .toList();
  }

  public TeacherDto getTeacherById(Long id) {
    return teacherRepository.findById(id)
        .map(teacherMapper::mapToDto)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
  }

  public TeacherDto createTeacher(TeacherDto teacherDto) {
    Teacher teacher = teacherMapper.mapToEntity(teacherDto);
    Teacher saved = teacherRepository.save(teacher);
    cacheInvalidationService.onTeacherChanged();
    return teacherMapper.mapToDto(saved);
  }

  public TeacherDto updateTeacher(Long id, TeacherDto teacherDto) {
    Teacher existing = teacherRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG + id));
    existing.setName(teacherDto.getName());
    existing.setDepartment(teacherDto.getDepartment());
    Teacher saved = teacherRepository.save(existing);
    cacheInvalidationService.onTeacherChanged();
    return teacherMapper.mapToDto(saved);
  }

  public void deleteTeacher(Long id) {
    teacherRepository.deleteById(id);
    cacheInvalidationService.onTeacherChanged();
  }

  public Page<TeacherDto> searchTeachers(
      String nameFilter,
      String departmentFilter,
      String courseCategory,
      Pageable pageable,
      QueryMode queryMode) {
    String normalizedNameFilter = normalizeLike(nameFilter);
    String normalizedDepartmentFilter = normalizeLike(departmentFilter);
    String normalizedCourseCategory = normalizeLike(courseCategory);
    TeacherQueryCacheKey key = TeacherQueryCacheKey.from(
        queryMode,
        normalizedNameFilter,
        normalizedDepartmentFilter,
        normalizedCourseCategory,
        pageable);
    return teacherQueryCache.getOrLoad(
        key,
        () -> fetchTeachersWithFilters(
            normalizedNameFilter,
            normalizedDepartmentFilter,
            normalizedCourseCategory,
            pageable,
            queryMode));
  }

  private Page<TeacherDto> fetchTeachersWithFilters(
      String nameFilter,
      String departmentFilter,
      String courseCategory,
      Pageable pageable,
      QueryMode queryMode) {
    if (queryMode == QueryMode.NATIVE) {
      Page<Teacher> nativePage = teacherRepository.findWithFiltersNative(
          nameFilter, departmentFilter, courseCategory, pageable);
      return mapTeacherPage(nativePage, pageable);
    }
    return teacherRepository.findWithFilters(nameFilter, departmentFilter, courseCategory, pageable)
        .map(teacherMapper::mapToDto);
  }

  private Page<TeacherDto> mapTeacherPage(Page<Teacher> teacherPage, Pageable pageable) {
    return new PageImpl<>(
        teacherPage.stream()
            .map(teacherMapper::mapToDto)
            .toList(),
        pageable,
        teacherPage.getTotalElements());
  }

  // cache invalidation handled by CacheInvalidationService

  private static String normalizeLike(String value) {
    return value == null ? "" : value;
  }
}
